#pragma once

#include <stdio.h>
#include <string>
#include <vector>
#include <WS2tcpip.h>
#include "process.h"
#include "helpers.h"
#define BUFF_SIZE 2048
using namespace std;

int Receive(SOCKET, char *, int, int);
int Send(SOCKET, char *, int, int);

/*
* @function byte_stream_sender: send message in byte_stream mode
* @param client(SOCKET): client socket
* @param message(string): send message
* @no return 
*/
void byte_stream_sender(SOCKET client, string message);

/*
* @function byte_stream_receiver: push receive message to message queue
* @param message_queue(string*): message queue
* @param received_message(string): request message
* @return extracted message
*/
string byte_stream_receiver(string* message_queue, string received_message);

/*
* @function server_notification: send message to all users inside room
* @param room_id(string): room id
* @param message(string): message detail
* @param room(vector<Room>*): created room list
* @no return
*/
void server_notification(string room_id, string message, vector<Room> *rooms);