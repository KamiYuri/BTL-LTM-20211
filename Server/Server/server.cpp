// WSAEventSelectServer.cpp : Defines the entry point for the console application.
//
#include "status_code.h"
#include "helpers.h"
#include <stdio.h>
#include <conio.h>
#include <WinSock2.h>
#include <WS2tcpip.h>
#include <string>
#include <vector>
#include <chrono>
#include <ctime>
#include <fstream>
#include "process.h"
#pragma comment(lib, "Ws2_32.lib")

#define SERVER_ADDR "127.0.0.1"
#define PORT 5500
#define BUFF_SIZE 2048

using namespace std;

char sendBuff[BUFF_SIZE], recvBuff[BUFF_SIZE], buff[BUFF_SIZE];
int ret;
// data structure declaration
struct User {
	string user_id;
	string username;
	string password;
	SOCKET socket;
	char client_ip[INET_ADDRSTRLEN];
	int client_port;
};

struct Room {
	string client[WSA_MAXIMUM_WAIT_EVENTS];
	string room_id;
	string item_name;
	string item_description;
	int starting_price;
	int buy_immediately_price;
	int current_price;
	int owner = -1;
};
unsigned __stdcall echoThread(void* param) {
	clientInfo client = *(clientInfo *)param;
	cout << client.clientIP << " " << client.clientPort << endl;
	char buff[BUFF_SIZE];
	string dataQueue = "";
	int ret;
	SOCKET connectedSocket = (SOCKET)client.connSock;
	while (1)
	{
		memset(&buff, 0, sizeof(buff));
		//receive message from client
		ret = recv(connectedSocket, buff, BUFF_SIZE, 0);
		if (ret == SOCKET_ERROR) {
			printf("Error %d: Cannot receive data \n", WSAGetLastError());
			response suddenRes = logout(client.username, &client);
			suddenRes.message = "QUIT ";
			write(suddenRes, &client);

			break;
		}
		else if (ret == 0) {
			printf("Client disconnects\n");
			break;
		}
		else
		{
			buff[ret] = 0;
			string tmpBuff = buff;
			dataQueue = byteStreamHandle(&tmpBuff[0], &client, dataQueue, buff);
		}
	}

	closesocket(connectedSocket);
	return 0;
}

vector<User> *users = new vector<User>();
vector<Room> *rooms = new vector<Room>();
int user_id_count = 0;
int room_count = 0;

// change room array to vector to have unlimited size
void filter_request(string message, SOCKET client_socket);
void log_in_handler(string email, string password, SOCKET client_socket);
void log_out_handler(string user_id);
void show_rooms_handler();
void join_room_handler(string room_id, string user_id);
void bid_handler(int price, string room_id, string user_id);
void buy_immediately_handler(string room_id, string user_id);
void create_room_handler(
	string item_name,
	string item_description,
	int starting_price,
	int buy_immediately_price);

void log_in_handler(string email, string password, char client_ip[INET_ADDRSTRLEN], int client_port, SOCKET client_socket) {
	string message = login(email, password, client_ip, client_port, client_socket, users, &user_id_count);
	strcpy_s(buff, message.length()+1, &message[0]);
	ret = send(client_socket, buff, strlen(buff), 0);
	if (ret == SOCKET_ERROR)
		printf("Error %d", WSAGetLastError());
};
void log_out_handler(string user_id, SOCKET client_socket) {
	string message = logout(user_id, users);
	strcpy_s(buff, message.length() + 1, &message[0]);
	ret = send(client_socket, buff, strlen(buff), 0);
	if (ret == SOCKET_ERROR)
		printf("Error %d", WSAGetLastError());
};
void show_rooms_handler() {};
void join_room_handler(string room_id, string user_id, SOCKET client_socket) {
	string message = join_room(room_id, user_id, rooms);
	strcpy_s(buff, message.length() + 1, &message[0]);
	ret = send(client_socket, buff, strlen(buff), 0);
	if (ret == SOCKET_ERROR)
		printf("Error %d", WSAGetLastError());
};
void bid_handler(int price, string room_id, string user_id) {};
void buy_immediately_handler(string room_id, string user_id) {};

void create_room_handler(
	string item_name,
	string item_description,
	int starting_price,
	int buy_immediately_price) {
	_beginthreadex(0, 0, echoThread, (void *)&client_socket, 0, 0); //start thread

};


int Receive(SOCKET, char *, int, int);
int Send(SOCKET, char *, int, int);

int main(int argc, char* argv[])
{
	DWORD		nEvents = 0;
	DWORD		index;
	SOCKET		socks[WSA_MAXIMUM_WAIT_EVENTS];
	WSAEVENT	events[WSA_MAXIMUM_WAIT_EVENTS];
	WSANETWORKEVENTS sockEvent;

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

	socks[0] = listenSock;
	events[0] = WSACreateEvent(); //create new events
	nEvents++;

	// Associate event types FD_ACCEPT and FD_CLOSE
	// with the listening socket and newEvent   
	WSAEventSelect(socks[0], events[0], FD_ACCEPT | FD_CLOSE);


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

	SOCKET connSock;
	sockaddr_in clientAddr;
	int clientAddrLen = sizeof(clientAddr);
	int ret, i;

	for (i = 1; i < WSA_MAXIMUM_WAIT_EVENTS; i++) {
		socks[i] = 0;
	}
	while (1) {
		//wait for network events on all socket
		index = WSAWaitForMultipleEvents(nEvents, events, FALSE, WSA_INFINITE, FALSE);
		if (index == WSA_WAIT_FAILED) {
			printf("Error %d: WSAWaitForMultipleEvents() failed\n", WSAGetLastError());
			break;
		}

		index = index - WSA_WAIT_EVENT_0;
		WSAEnumNetworkEvents(socks[index], events[index], &sockEvent);

		//reset event
		WSAResetEvent(events[index]);

		if (sockEvent.lNetworkEvents & FD_ACCEPT) {
			if (sockEvent.iErrorCode[FD_ACCEPT_BIT] != 0) {
				printf("FD_ACCEPT failed with error %d\n", sockEvent.iErrorCode[FD_READ_BIT]);
				break;
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
				printf("\nToo many clients.");
				closesocket(connSock);
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
				break;
			}

			ret = Receive(socks[index], recvBuff, BUFF_SIZE, 0);

			//Release socket and event if an error occurs
			if (ret <= 0) {
				closesocket(socks[index]);
				WSACloseEvent(events[index]);

				socks[index] = socks[nEvents - 1];
				events[index] = events[nEvents - 1];
				nEvents--;
			}
			else {								
				//echo to client
				recvBuff[ret] = 0;
				string message = recvBuff;
                // chưa có truyền dòng
				filter_request(message, socks[index]);
				//memcpy(sendBuff, recvBuff, ret);
				//Send(socks[index], sendBuff, ret, 0);

				//reset event
				WSAResetEvent(events[index]);
			}
		}

		if (sockEvent.lNetworkEvents & FD_CLOSE) {
			if (sockEvent.iErrorCode[FD_CLOSE_BIT] != 0) {
				printf("FD_CLOSE failed with error %d\n", sockEvent.iErrorCode[FD_CLOSE_BIT]);
				break;
			}
			//Release socket and event
			closesocket(socks[index]);
			socks[index] = 0;
			WSACloseEvent(events[index]);
			nEvents--;
		}
	}
	return 0;
}

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
		log_out_handler(user_id);

	}
	else if (method == "SHOW__") {
		show_rooms_handler();
	}
	else if (method == "JOIN__") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		join_room_handler(room_id, user_id);
	}
	else if (method == "BID___") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		int price = stoi(payload.substr(0, spliting_delimiter_index));

		int pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		string user_id = payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);

		bid_handler(price, room_id, user_id);
	}
	else if (method == "BUYNOW") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string user_id = payload.substr(0, spliting_delimiter_index);
		string room_id = payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2);
		buy_immediately_handler(room_id, user_id);
	}
	else if (method == "CREATE") {
		int spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1);
		string item_name = payload.substr(0, spliting_delimiter_index);

		int pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		string item_description = payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2);

		pre_delimiter_index = spliting_delimiter_index;
		spliting_delimiter_index = payload.find(SPLITING_DELIMITER_1, pre_delimiter_index + 2);
		int starting_price = stoi(payload.substr(pre_delimiter_index + 2, spliting_delimiter_index - pre_delimiter_index - 2));
		int buy_immediately_price = stoi(payload.substr(spliting_delimiter_index + 2, payload.length() - spliting_delimiter_index - 2));
	}
