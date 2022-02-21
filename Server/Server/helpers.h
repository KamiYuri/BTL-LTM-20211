#pragma once

#include <stdio.h>
#include <string>
#include <vector>
#include <WS2tcpip.h>
#include "process.h"
using namespace std;

struct User {
	string user_id;
	SOCKET socket;
	string joined_room_id = "-1";
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
	string owner = "-1"; 
	// = -1 if no one has bought during bid time,
	// = 0 if no one has bought after bid time, 
	// = x if user with id = x has bought after bid time,
	HANDLE timer_thread;
};

/*
* @function login: verify email and password
* @param email(string): room id
* @param password(string): user id
* @param client_socket(SOCKET): contain socket of request user

* @return response code (defined in status_code.h)
*/
string login(string email, string password, SOCKET client_socket, vector<User> *users);

/*
* @function logout: delete user from user list
* @param user_id(string): user id
* @param user(vector<User>*): connected user list
* @param room(vector<Room>*): created room list
* @return response code (defined in status_code.h)
*/
string logout(string user_id, vector<User> *users, vector<Room> *rooms);

/*
* @function show_room: display all created room
* @param room(vector<Room>*): created room list
* @return response code (defined in status_code.h)
*/
string show_room(vector<Room> *rooms);

/*
* @function join_room: add user to room
* @param room_id(string): room id
* @param user_id(string): user id
* @param room(vector<Room>*): created room list
* @param user(vector<User>*): connected user list
* @return response code (defined in status_code.h)
*/
string join_room(string room_id, string user_id, vector<Room> *rooms, vector<User> *users);

/*
* @function bid: reset timer thread and update new price, or refuse if information is invalid
* @param room_id(string): room id
* @param user_id(string): user id
* @param room(vector<Room>*): created room list
* @return response code (defined in status_code.h)
*/
string bid(int price, string room_id, string user_id, vector<Room> *rooms);

/*
* @function buy_immediately: end timer thread immediately and update new owner, or refuse if information is invalid
* @param room_id(string): room id
* @param user_id(string): user id
* @param room(vector<Room>*): created room list
* @return response code (defined in status_code.h)
*/
string buy_immediately(string room_id, string user_id, vector<Room> *rooms);

/*
* @function create_room: start new timer thread, or refuse if information is invalid
* @param user_id(string): user id
* @param item_name(string): name of the item
* @param item_description(string): description of the item
* @param starting_price(int): starting price of the item
* @param buy_immediately_price(int): price that user can buy immediately
* @param room(vector<Room>*): created room list
* @param id_count(int*): room of previous created room id, use to auto generate room id
* @return response code (defined in status_code.h)
*/
string create_room(string user_id, string item_name, string item_description, int starting_price, int buy_immediately_price, vector<Room> *rooms, int *id_count);

/*
* @function leave_room: delete user from room's client list
* @param room_id(string): room id
* @param user_id(string): user id
* @param room(vector<Room>*): created room list
* @param user(vector<User>*): connected user list
* @return response code (defined in status_code.h)
*/
string leave_room(string room_id, string user_id, vector<Room> *rooms, vector<User> *users);


