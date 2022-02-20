#pragma once

#include <stdio.h>
#include <string>
#include <vector>
#include <WS2tcpip.h>
#include "process.h"
using namespace std;

struct User {
	string user_id;
	//string username;
	//string password;
	SOCKET socket;
	string joined_room_id = "-1";
	//char client_ip[INET_ADDRSTRLEN];
	//int client_port;
};

struct Room {
	string room_creator_id;
	vector<User> client_list;   //array of users
	string room_id;
	string item_name;
	string item_description;
	int starting_price;
	int buy_immediately_price;
	int current_price;
	string current_highest_user = "0";
	string owner = "-1"; //default = "-1", if item was sold then = user_id // = 0  if after time-out, no one buy
	HANDLE timer_thread;
};

/*
* @function login: verify email and password
* @param email(string): room id
* @param password(string): user id
* @param client_socket(SOCKET): contain socket of request user

* @no return
*/
string login(string email, string password, SOCKET client_socket, vector<User> *users);
string logout(string user_id, vector<User> *users, vector<Room> *rooms);
string show_room(vector<Room> *rooms);
string join_room(string room_id, string user_id, vector<Room> *rooms, vector<User> *users);
string bid(int price, string room_id, string user_id, vector<Room> *rooms);
string buy_immediately(string room_id, string user_id, vector<Room> *rooms);
string create_room(string user_id, string item_name, string item_description, int starting_price, int buy_immediately_price, vector<Room> *rooms, int *id_count);
string leave_room(string room_id, string user_id, vector<Room> *rooms, vector<User> *users);


