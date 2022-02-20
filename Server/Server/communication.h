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
void byte_stream_sender(SOCKET client, string message);
string byte_stream_receiver(string* message_queue, string received_message);
void server_notification(string room_id, string message, vector<Room> *rooms);