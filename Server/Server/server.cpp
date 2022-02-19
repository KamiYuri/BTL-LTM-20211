// WSAEventSelectServer.cpp : Defines the entry point for the console application.
//
#include "status_code.h"
#include "helpers.h"
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

#define  WSA_MAXIMUM_WAIT_EVENTS 2
#define SERVER_ADDR "127.0.0.1"
#define PORT 5500
#define BUFF_SIZE 2048
using namespace std;

char send_buff[BUFF_SIZE], recv_buff[BUFF_SIZE], buff[BUFF_SIZE];
string message_queue[10000]; //  temp use, change size later
int ret;
HANDLE hthread;

vector<User> users;
vector<Room> rooms;
int user_id_count = 1;
int room_id_count = 1;
// data structure declaration

void filter_request(string message, SOCKET client_socket);
void log_in_handler(string email, string password, SOCKET client_socket);
void log_out_handler(string user_id, SOCKET client_socket);
void show_rooms_handler(SOCKET client_socket);
void join_room_handler(string room_id, string user_id, SOCKET client_socket);
void bid_handler(int price, string room_id, string user_id, SOCKET client_socket);
void buy_immediately_handler(string room_id, string user_id, SOCKET client_socket);
void create_room_handler(
	string user_id,
	string item_name,
	string item_description,
	int starting_price,
	int buy_immediately_price,
	SOCKET client_socket);
int Receive(SOCKET, char *, int, int);
int Send(SOCKET, char *, int, int);
void byte_stream_sender(SOCKET client, string message);
string byte_stream_receiver(string* message_queue, string received_message);
unsigned __stdcall timer_thread(void *param);
unsigned __stdcall worker_thread(void *param);

int main(int argc, char* argv[])
{
	//Step 1: Initiate WinSock
	WSADATA wsaData;
	WORD wVersion = MAKEWORD(2, 2);
	if (WSAStartup(wVersion, &wsaData)) {
		printf("Winsock 2.2 is not supported\n");
		return 0;
	}

	//Step 2: Construct LISTEN socket	
	SOCKET listenSock;
	listenSock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);

	//Step 3: Bind address to socket
	sockaddr_in serverAddr;
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(PORT);
	inet_pton(AF_INET, SERVER_ADDR, &serverAddr.sin_addr);


	if (bind(listenSock, (sockaddr *)&serverAddr, sizeof(serverAddr)))
	{
		printf("Error %d: Cannot associate a local address with server socket.", WSAGetLastError());
		return 0;
	}

	//Step 4: Listen request from client
	if (listen(listenSock, 10)) {
		printf("Error %d: Cannot place server socket in state LISTEN.", WSAGetLastError());
		return 0;
	}

	printf("Server started!\n");


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
	DWORD		nEvents = 0;
	DWORD		index;
	SOCKET		socks[WSA_MAXIMUM_WAIT_EVENTS];
	WSAEVENT	events[WSA_MAXIMUM_WAIT_EVENTS];
	WSANETWORKEVENTS sockEvent;
	int thread_capacity_is_full = 0;

	SOCKET connSock;
	sockaddr_in clientAddr;
	int clientAddrLen = sizeof(clientAddr);
	int ret, i;
	//get listenSock from param
	SOCKET listenSock = ((SOCKET*)param)[0];
	//get connSock from param if exists( not equal INVALID_SOCKET)
	if (((SOCKET*)param)[1] != INVALID_SOCKET)
	{
		SOCKET connSock = ((SOCKET*)param)[1];
		//add connSock to array of client, then create an event and assign it to connSock with reading and closing event
		socks[1] = connSock;
		events[1] = WSACreateEvent();
		cout << "Accept new client from " << endl;
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

	for (i = 1; i < WSA_MAXIMUM_WAIT_EVENTS; i++) {
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
				// handle client error here
			}

			if ((connSock = accept(socks[index], (sockaddr *)&clientAddr, &clientAddrLen)) == SOCKET_ERROR) {
				printf("Error %d: Cannot permit incoming connection.\n", WSAGetLastError());
				// handle client error here
				break;
			}

			//Add new socket into socks array
			int i;
			if (nEvents == WSA_MAXIMUM_WAIT_EVENTS) {
				//check if there was no thread was created before
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
				nEvents--;
			}
			else {
				recv_buff[ret] = 0;
				string received_message = recv_buff;
				string message = byte_stream_receiver(message_queue, received_message);
				filter_request(message, socks[index]);

				//reset event
				WSAResetEvent(events[index]);
			}
		}

		if (sockEvent.lNetworkEvents & FD_CLOSE) {
			if (sockEvent.iErrorCode[FD_CLOSE_BIT] != 0) {
				printf("FD_CLOSE failed with error %d\n", sockEvent.iErrorCode[FD_CLOSE_BIT]);
			}
			// logout handler here
			//Release socket and event
			closesocket(socks[index]);
			socks[index] = 0;
			WSACloseEvent(events[index]);
			nEvents--;
		}
	}
	WaitForSingleObject(worker_thread_handler, INFINITE);
	return 0;

}

unsigned __stdcall timer_thread(void *param) {
	int count = 0;
	string room_id = (char *)param;
	while (1) {
		int test_time = 10000;
		Sleep(test_time);
		for (int i = 0;i < rooms.size();i++) {
			if (rooms[i].room_id == room_id)
			{
				vector<User> participants = rooms[i].client_list;
				for (int j = 0;j < participants.size();j++) {
					string message = "user id: " + rooms[i].current_highest_user +
						"+ current price: " + to_string(rooms[i].current_price) +
						"+ participants number:" + to_string(participants.size());
					strcpy_s(buff, message.length() + 1, &message[0]);
					ret = send(participants[j].socket, buff, strlen(buff), 0);
					if (ret == SOCKET_ERROR)
					{
						printf("Error %d: Cannot send data.\n", WSAGetLastError());
					}
				}
			}
		}
	}
	return 0;
}

void log_in_handler(string email, string password, SOCKET client_socket) {
	string message = login(email, password, client_socket, &users, &user_id_count);
	byte_stream_sender(client_socket, message);
};
void log_out_handler(string user_id, SOCKET client_socket) {
	string message = logout(user_id, &users);
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
		if (ret == SOCKET_ERROR)
			printf("Error %d", WSAGetLastError());
	}
	else {

	}
};
void buy_immediately_handler(string room_id, string user_id, SOCKET client_socket) {
	string message = buy_immediately(room_id, user_id, &rooms);
	byte_stream_sender(client_socket, message);
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

string byte_stream_receiver(string* message_queue, string received_message) {
	*message_queue = *message_queue + received_message;
	string user_message;
	int delimiter_position = (*message_queue).find(ENDING_DELIMITER);
	// if there is data end signal => handle
	if (delimiter_position != -1)
		user_message = (*message_queue).substr(0, delimiter_position);

	*message_queue = (*message_queue).substr(delimiter_position + 2, (*message_queue).length() - delimiter_position);
	return user_message;
}

void byte_stream_sender(SOCKET client, string message) {
	message = message + ENDING_DELIMITER;
	int message_length = message.length();
	int left_bytes = message_length;
	int index = 0;

	//split message to send when message's size is larger than buff size
	while (left_bytes != 0)
	{
		// if left message size <  buff_size => send everything left
		if (left_bytes <= BUFF_SIZE)
		{
			string tmp = message.substr(index, left_bytes);
			strcpy_s(send_buff, tmp.length() + 1, &tmp[0]);
			left_bytes = 0;
			ret = Send(client, send_buff, strlen(send_buff), 0);
			if (ret == SOCKET_ERROR)
				printf("Error %d", WSAGetLastError());
		}
		else
		{
			// send parts of message
			string tmp = message.substr(index, BUFF_SIZE);
			strcpy_s(send_buff, tmp.length() + 1, &tmp[0]);
			ret = Send(client, send_buff, strlen(send_buff), 0);
			if (ret == SOCKET_ERROR)
				printf("Error %d", WSAGetLastError());
			left_bytes = left_bytes - ret;
			index = index + ret;
		}
	}
};

/* The recv() wrapper function */
int Receive(SOCKET s, char *buff, int size, int flags) {
	int n;

	n = recv(s, buff, size, flags);
	if (n == SOCKET_ERROR)
		printf("Error %d: Cannot receive data.\n", WSAGetLastError());
	else if (n == 0)
		printf("Client disconnects.\n");
	return n;
}

/* The send() wrapper function*/
int Send(SOCKET s, char *buff, int size, int flags) {
	int n;

	n = send(s, buff, size, flags);
	if (n == SOCKET_ERROR)
		printf("Error %d: Cannot send data.\n", WSAGetLastError());

	return n;
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
}
