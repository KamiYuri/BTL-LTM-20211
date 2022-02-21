
#include "communication.h"
#include "status_code.h"
#include "helpers.h"
#include <iostream>
using namespace std;
char send_buff[BUFF_SIZE];
string byte_stream_receiver(string* message_queue, string received_message) {
	*message_queue = *message_queue + received_message;
	string user_message;
	int delimiter_position = (*message_queue).find(ENDING_DELIMITER);
	// if there is data end signal => handle
	if (delimiter_position != -1)
		user_message = (*message_queue).substr(0, delimiter_position);

	*message_queue = (*message_queue).substr(delimiter_position + 2, (*message_queue).length() - delimiter_position);
	cout << "RECEIVED: " << user_message << endl;
	return user_message;
}

void byte_stream_sender(SOCKET client, string message) {
	cout << "SEND: " << message << endl;
	message = message + ENDING_DELIMITER;
	int message_length = message.length();
	int left_bytes = message_length;
	int index = 0, ret;

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
	if (n == SOCKET_ERROR) {
		printf("%s \n", buff);
		printf("Error %d: Cannot send data.\n", WSAGetLastError());
	}

	return n;
}

void server_notification(string room_id, string message, vector<Room> *rooms) {
	for (int i = 0;i < (*rooms).size();i++) {
		if ((*rooms)[i].room_id == room_id) {
			vector<User> participants = (*rooms)[i].client_list;
			for (int j = 0;j < participants.size();j++)
				byte_stream_sender(participants[j].socket, message);
		}
	}
}