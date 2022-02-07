#pragma once

#include <stdio.h>
#include <string>
#include <vector>
#include <WS2tcpip.h>
using namespace std;

struct User {
	string user_id;
	string username;
	string password;
	SOCKET socket;
	char client_ip[INET_ADDRSTRLEN];
	int client_port;
};

struct room {
	vector<string> client_list;   //array of strings of user_id
	string room_id;
	string item_name;
	string item_description;
	int starting_price;
	int buy_immediately_price;
	int current_price;
	string current_highest_user;
	string owner = "-1"; //default = "-1", if item was sold then = user_id
};
string login(string email, string password, char client_ip[INET_ADDRSTRLEN], int client_port, SOCKET client_socket, vector<User> *users, int *user_id_count);
string logout(string user_id, vector<User> *users);
string show_room(vector<room> *rooms);
string join_room(string room_id, string user_id, vector<room> *rooms);
string bid(int price, string room_id, string user_id, vector<room> *rooms);
string buy_immediately(string room_id, string user_id, vector<room> *rooms);
string create_room(string item_name, string item_description, int starting_price, int buy_immediately_price, vector<room> *rooms);



