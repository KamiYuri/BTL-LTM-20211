// WSAEventSelectServer.cpp : Defines the entry point for the console application.
//
#include "status_code.h"
#include "helpers.h"
#include "communication.h"
#include <iostream>
#include <stdio.h>
#include <conio.h>
#include <WinSock2.h>
#include <Windows.h>
#include <WS2tcpip.h>
#include <string>
#include <vector>
#include <chrono>
#include <ctime>
#include <fstream>
#include "process.h"
#include <processthreadsapi.h>
#pragma comment(lib, "Ws2_32.lib")

#define SERVER_ADDR "127.0.0.1"
#define PORT 5500
using namespace std;

char recv_buff[BUFF_SIZE];
string message_queue ="";
int ret, room_id_count = 1;
HANDLE hthread;
vector<User> users;
vector<Room> rooms;

/*
* @function filter_request: send message payload to the suitable message handler based on message method
* @param message(string): message detail
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void filter_request(string message, SOCKET client_socket);

/*
* @function log_in_handler: verify user email and password based on account.txt
* @param email(string): user email
* @param password(string): user password
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void log_in_handler(string email, string password, SOCKET client_socket);

/*
* @function log_out_handler: delete user connection socket
* @param user_id(string): user id
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void log_out_handler(string user_id, SOCKET client_socket);

/*
* @function show_rooms_handler: display all created rooms
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void show_rooms_handler(SOCKET client_socket);

/*
* @function join_room_handler: add user to a room
* @param room_id(string): room id
* @param user_id(string): user id
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void join_room_handler(string room_id, string user_id, SOCKET client_socket);

/*
* @function bid_handler: update a new bid price, reset timer thread
* @param room_id(string): room id
* @param user_id(string): user id
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void bid_handler(int price, string room_id, string user_id, SOCKET client_socket);

/*
* @function buy_immediately_handler: set new owner of the item, and timer thread immediately
* @param room_id(string): room id
* @param user_id(string): user id
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void buy_immediately_handler(string room_id, string user_id, SOCKET client_socket);

/*
* @function create_room_handler: create and run new timer thread, create new room in rooms list
* @param user_id(string): user id
* @param item_name(string): name of the item
* @param item_description(string): description of the item
* @param starting_price(int): starting price of the item
* @param buy_immediately_price(int): price that user can buy immediately
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void create_room_handler(
	string user_id,
	string item_name,
	string item_description,
	int starting_price,
	int buy_immediately_price,
	SOCKET client_socket);

/*
* @function leave_room_handler: remove user from participant list of that room
* @param room_id(string): room id
* @param user_id(string): user id
* @param client_socket(SOCKET): contain socket of request user
* @no return
*/
void leave_room_handler(string room_id, string user_id, SOCKET client_socket);
 
/*
 * @thread timer_thread: time count and notify the owner when the time is over
 */
unsigned __stdcall timer_thread(void *param);

/*
* @thread worker_thread: handle connection, new children worker_thread will be created when the number of parent thread exccess maximum number of 64 clients
*/
unsigned __stdcall worker_thread(void *param);

int main(int argc, char* argv[])
{
	//Initiate WinSock
	WSADATA wsaData;
	WORD wVersion = MAKEWORD(2, 2);
	if (WSAStartup(wVersion, &wsaData)) {
		printf("Winsock 2.2 is not supported\n");
		return 0;
	}

	//Construct LISTEN socket	
	SOCKET listenSock;
	listenSock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	//Bind address to socket
	sockaddr_in serverAddr;
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(PORT);
	inet_pton(AF_INET, SERVER_ADDR, &serverAddr.sin_addr);


	if (bind(listenSock, (sockaddr *)&serverAddr, sizeof(serverAddr)))
	{
		printf("Error %d: Cannot associate a local address with server socket.", WSAGetLastError());
		return 0;
	}

	// Listen request from client
	if (listen(listenSock, 10)) {
		printf("Error %d: Cannot place server socket in state LISTEN.", WSAGetLastError());
		return 0;
	}

	printf("Server started!\n");

	// bind listen sock to the first worker thread
	SOCKET param[2];
	param[0] = listenSock;
	param[1] = INVALID_SOCKET;

	_beginthreadex(0, 0, worker_thread, (void*)param, 0, 0);
	while (1) {
		// keep the server loop 
	}
	return 0;
}

unsigned __stdcall worker_thread(void *param) {
	// resource initialization
	DWORD		nEvents = 0;
	DWORD		index;
	SOCKET		socks[WSA_MAXIMUM_WAIT_EVENTS];
	WSAEVENT	events[WSA_MAXIMUM_WAIT_EVENTS];
	WSANETWORKEVENTS sockEvent;
	SOCKET connSock;
	sockaddr_in clientAddr;
	int thread_capacity_is_full = 0, ret, clientAddrLen = sizeof(clientAddr);

	//get listenSock from parent worker thread
	SOCKET listenSock = ((SOCKET*)param)[0];
	if (((SOCKET*)param)[1] != INVALID_SOCKET)
	{
		SOCKET connSock = ((SOCKET*)param)[1];
		//add connSock to array of clients, 
		//create an event and assign it to connSock with reading and closing event
		socks[1] = connSock;
		events[1] = WSACreateEvent();
		cout << "New client connected" << endl;
		WSAEventSelect(socks[1], events[1], FD_READ | FD_CLOSE);
		nEvents++;
	}
	//set first element of client array with listenSock
	socks[0]= listenSock;
	events[0] = WSACreateEvent(); //create new events
	nEvents++;

	// Assign an event types FD_ACCEPT and FD_CLOSE
	// with the listening socket and newEvent   
	WSAEventSelect(socks[0], events[0], FD_ACCEPT | FD_CLOSE);

	for (int i = 1; i < WSA_MAXIMUM_WAIT_EVENTS; i++) {
		if (i == 1 && ((SOCKET*)param)[1] != INVALID_SOCKET) 
			continue;
		socks[i] = 0;
	}

	HANDLE worker_thread_handler;
	while (1) {
		//wait for network events on all socket
		index = WSAWaitForMultipleEvents(nEvents, events, FALSE, WSA_INFINITE, FALSE);
		if (index == WSA_WAIT_FAILED) {
			printf("Error %d: WSAWaitForMultipleEvents() failed\n", WSAGetLastError());
		}

		index = index - WSA_WAIT_EVENT_0;
		WSAEnumNetworkEvents(socks[index], events[index], &sockEvent);

		//reset event
		WSAResetEvent(events[index]);

		if (sockEvent.lNetworkEvents & FD_ACCEPT) {
			if (sockEvent.iErrorCode[FD_ACCEPT_BIT] != 0) {
				printf("FD_ACCEPT failed with error %d\n", sockEvent.iErrorCode[FD_READ_BIT]);
			}

			if ((connSock = accept(socks[index], (sockaddr *)&clientAddr, &clientAddrLen)) == SOCKET_ERROR) {
				printf("Error %d: Cannot permit incoming connection.\n", WSAGetLastError());
			}

			//Add new socket into socks array
			int i;
			if (nEvents == WSA_MAXIMUM_WAIT_EVENTS) {
				//check if there is no thread was created before
				if (thread_capacity_is_full == 0)
				{
					SOCKET param[2];
					param[0] = listenSock;
					param[1] = connSock;
					printf("Maximum clients reached: new worker thread will be created.\n");
					thread_capacity_is_full = 1;
					worker_thread_handler = (HANDLE)_beginthreadex(0, 0, worker_thread, (void*)param, 0, 0);
				}
			}
			else {
				socks[nEvents] = connSock;
				events[nEvents] = WSACreateEvent();
				WSAEventSelect(socks[nEvents], events[nEvents], FD_READ | FD_CLOSE);
				nEvents++;
			}
			for (i = 1; i < WSA_MAXIMUM_WAIT_EVENTS; i++)
				if (socks[i] == 0) {
					socks[i] = connSock;
					events[i] = WSACreateEvent();
					WSAEventSelect(socks[i], events[i], FD_READ | FD_CLOSE);
					nEvents++;
					break;
				}
		}

		if (sockEvent.lNetworkEvents & FD_READ) {
			//Receive message from client
			if (sockEvent.iErrorCode[FD_READ_BIT] != 0) {
				printf("FD_READ failed with error %d\n", sockEvent.iErrorCode[FD_READ_BIT]);
			}

			ret = Receive(socks[index], recv_buff, BUFF_SIZE, 0);

			//Release socket and event if an error occurs
			if (ret <= 0) {
				closesocket(socks[index]);
				WSACloseEvent(events[index]);
				socks[index] = socks[nEvents - 1];
				events[index] = events[nEvents - 1];
				socks[nEvents - 1] = 0;
				events[nEvents - 1] = 0;
				nEvents--;
			}
			else {
				recv_buff[ret] = 0;
				string received_message = recv_buff;
				string message = byte_stream_receiver(&message_queue, received_message);
				filter_request(message, socks[index]);

				//reset event
				WSAResetEvent(events[index]);
			}
		}

		if (sockEvent.lNetworkEvents & FD_CLOSE) {
			if (sockEvent.iErrorCode[FD_CLOSE_BIT] != 0) {
				printf("An account has unexpectedly disconnected");
			}
			// log out user from joined room
			for (int i = 0; i < users.size(); i++)
				if (users[i].socket == socks[index])
					string ignored_response = leave_room(users[i].joined_room_id, users[i].user_id, &rooms, &users);
			//Release socket and event
			closesocket(socks[index]);
			WSACloseEvent(events[index]);
			socks[index] = socks[nEvents -1];
			events[index] = events[nEvents - 1];
			socks[nEvents - 1] = 0;
			events[nEvents - 1] = 0;
			nEvents--;
		}
	}
	WaitForSingleObject(worker_thread_handler, INFINITE);
	return 0;

}

unsigned __stdcall timer_thread(void *param) {
	int count = 0;
	string room_id = (char *)param;
	while (count < 3) {
		int test_time = 30000;
		Sleep(test_time);
		count++;
		string message = TIME_NOTIFICATION + to_string(3 - count);
		server_notification(room_id, message, &rooms);
	}

	// set user_id as an owner when the time is over
	for (int i = 0;i < rooms.size();i++)
		if (rooms[i].room_id == room_id) {
			rooms[i].owner = rooms[i].current_highest_user;
			server_notification(room_id, ITEM_SOLD_NOTIFICATION + rooms[i].owner, &rooms);
		}

	return 0;
}


void log_in_handler(string email, string password, SOCKET client_socket) {
	string message = login(email, password, client_socket, &users);
	byte_stream_sender(client_socket, message);
};
void log_out_handler(string user_id, SOCKET client_socket) {
	string message = logout(user_id, &users, &rooms);
	byte_stream_sender(client_socket, message);
};
void show_rooms_handler(SOCKET client_socket) {
	string message = show_room(&rooms);
	byte_stream_sender(client_socket, message);
};
void join_room_handler(string room_id, string user_id, SOCKET client_socket) {
	string message = join_room(room_id, user_id, &rooms, &users);
	byte_stream_sender(client_socket, message);
};
void bid_handler(int price, string room_id, string user_id, SOCKET client_socket) {
	string message = bid(price, room_id, user_id, &rooms);
	if (message.substr(0, 2) == SUCCESS_BID) {
		// timer thread reset
		int room_index = stoi(message.substr(2, message.length() - 2));
		TerminateThread(rooms[room_index].timer_thread, 0);
		char* room_id_in_char = (char*)malloc(sizeof(char) * 1000);
		strcpy_s(room_id_in_char, room_id.length() + 1, &room_id[0]);
		hthread = (HANDLE)_beginthreadex(0, 0, timer_thread, (void *)room_id_in_char, 0, 0); //start thread
		rooms[room_index].timer_thread = hthread;

		byte_stream_sender(client_socket, message);
		server_notification(room_id, NEW_PRICE_NOTIFICATION + to_string(price), &rooms);
	}
	else {
		byte_stream_sender(client_socket, message);
	}
};
void buy_immediately_handler(string room_id, string user_id, SOCKET client_socket) {
	string message = buy_immediately(room_id, user_id, &rooms);
	if (message.substr(0, 2) == SUCCESS_BUY_IMMEDIATELY) {
		int room_index = stoi(message.substr(2, message.length() - 2));
		TerminateThread(rooms[room_index].timer_thread, 0);

		byte_stream_sender(client_socket, message);
		server_notification(room_id, ITEM_SOLD_NOTIFICATION + rooms[room_index].owner, &rooms);
	}
	else {
		byte_stream_sender(client_socket, message);
	}
};

void create_room_handler(
	string user_id,
	string item_name,
	string item_description,
	int starting_price,
	int buy_immediately_price,
	SOCKET client_socket) {

	string response = create_room(user_id, item_name, item_description, starting_price, buy_immediately_price, &rooms, &room_id_count);
	if (response.substr(0, 2) == SUCCESS_CREATE_ROOM)
	{
		int delimiter_index = response.find(SPLITING_DELIMITER_1);
		string room_id = response.substr(2, delimiter_index - 2);
		cout << "Room created with id:" << room_id << endl;
		int room_index = stoi(response.substr(delimiter_index + 2, response.length() - delimiter_index));

		char* room_id_in_char = (char*)malloc(sizeof(char) * 1000);
		strcpy_s(room_id_in_char, room_id.length() + 1, &room_id[0]);

		hthread = (HANDLE)_beginthreadex(0, 0, timer_thread, (void *)room_id_in_char, 0, 0); //start thread
		rooms[room_index].timer_thread = hthread;
		byte_stream_sender(client_socket, response.substr(0, delimiter_index));
	}
	else
	{
		byte_stream_sender(client_socket, response);
	}

};

void leave_room_handler(string room_id, string user_id, SOCKET client_socket) {
	string message = leave_room(room_id, user_id, &rooms, &users);
	byte_stream_sender(client_socket, message);
}


void filter_request(string message, SOCKET client_socket) {
	string method = message.substr(0, 6);
	string payload = message.substr(6, message.length() - 5);

	if (method == "LOGIN_") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string email = payload.substr(0, spliting_delimiter_index);
		string password = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		log_in_handler(email, password, client_socket);
	}
	else if (method == "LOGOUT") {
		string user_id = payload;
		log_out_handler(user_id, client_socket);
	}
	else if (method == "SHOW__") {
		show_rooms_handler(client_socket);
	}
	else if (method == "JOIN__") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		join_room_handler(room_id, user_id, client_socket);
	}
	else if (method == "BID___") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		int price = stoi(payload.substr(0, spliting_delimiter_index));

		int pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		string user_id = payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);

		bid_handler(price, room_id, user_id, client_socket);
	}
	else if (method == "BUYNOW") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		buy_immediately_handler(room_id, user_id, client_socket);
	}
	else if (method == "CREATE") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);

		int pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		string item_name = payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2);

		pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		string item_description = payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2);

		pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		int starting_price = stoi(payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2));
		int buy_immediately_price = stoi(payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2));
		create_room_handler(user_id, item_name, item_description, starting_price, buy_immediately_price, client_socket);
	}
	else if (method == "LEAVE_") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		leave_room_handler(room_id, user_id, client_socket);
	}
}
