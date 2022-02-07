
#include "winsock2.h"
#include "windows.h"
#include "stdio.h"
#include "conio.h"
#include <WS2tcpip.h>
#include <algorithm>
#pragma comment (lib, "Ws2_32.lib")
#include "string.h"
#include "ws2tcpip.h"
#include "fstream"
#include "iostream"
#include "string"
#include "string.h"
#include "chrono"
#include "ctime"
#include "vector"
#include "helpers.h"
#include "status_code.h"

using namespace std;
struct User {
	string user_id;
	string username;
	string password;
	SOCKET socket;
	char client_ip[INET_ADDRSTRLEN];
	int client_port;
};

struct Room {
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
int id_count = 0;

string login(string email, string password, char client_ip[INET_ADDRSTRLEN], int client_port, SOCKET client_socket, vector<User> *users , int *user_id_count) {
	ifstream fileAcc;
	fileAcc.open("account.txt");
	if (fileAcc.is_open()) {
		string line;
		while (getline(fileAcc, line)) {
			int space = line.find(" ");
			string account = line.substr(0, space);
			string pass = line.substr(space + 1);
			if (email == account && password == pass) {
				//them cac thong tin vao 1 tmp_user roi them vao vector
				*user_id_count++;
				User tmp_user;
				tmp_user.user_id = *user_id_count;
				tmp_user.username = email;
				tmp_user.password = password;
				tmp_user.socket = client_socket;
				strcpy(tmp_user.client_ip, client_ip);
				tmp_user.client_port = client_port;
				(*users).push_back(tmp_user);
				return SUCCESS_LOGIN;
			}
			else return FAILED_LOGIN;
		}
	}
}
/*
struct find_user
{
	string user_id;
	find_user(string user_id) : user_id(user_id) {}
	bool operator () (const User& user) const
	{
		return user.user_id == user_id;
	}
};
*/
string logout(string user_id, vector<User> *users){
	for (int i = 0; i < (*users).size(); i++) {
		if ((*users)[i].user_id == user_id) {
			(*users).erase((*users).begin()+i-1);
			return SUCCESS_LOGOUT;
		}
	}
	return FAILED_LOGOUT;
}

string show_Room(vector<Room> &rooms) {
	string message;
	for (int i = 0;i<rooms.size();i++) {
		message = SUCCESS_SHOW_ROOM + rooms[i].room_id + SPLITING_DELIMITER_2 + rooms[i].item_name + SPLITING_DELIMITER_2 + rooms[i].item_description + SPLITING_DELIMITER_1;
	}
	message += ENDING_DELIMITER;
	return message;
}

string join_Room(string room_id, string user_id, vector<Room> *rooms) {
	for (int i = 0;i<(*rooms).size();i++) {
		if (room_id == (*rooms)[i].room_id) {
			(*rooms)[i].client_list.push_back(user_id);
			return SUCCESS_JOIN_ROOM;
		}
	}
	return ROOM_NOT_FOUND;

}

string bid(int price, string room_id, string user_id, vector<Room> &rooms) {
	for (int i = 0;i<rooms.size();i++) {
		if (rooms[i].room_id == room_id) {
			if (price > rooms[i].current_price) {
				rooms[i].current_price = price;
				rooms[i].current_highest_user = user_id;
				return SUCCESS_BID;
			}
			else return LOWER_THAN_CURRENT_PRICE;
		}
	}
	return "not available yet";
}

string buy_immediately(string room_id, string user_id, vector<Room> &rooms) {
	for (int i = 0;i<rooms.size();i++) {
		if (rooms[i].room_id == room_id) {
			if (rooms[i].owner == "-1") {
				rooms[i].owner == user_id;
				return SUCCESS_BUY_IMMEDIATELY;
			}
			else return ALREADY_SOLD;
		}
	}
	return "not available yet";
}

string create_Room(string item_name, string item_description, int starting_price, int buy_immediately_price, vector<Room> &rooms) {
	Room tmp_Room;
	tmp_Room.item_name = item_name;
	tmp_Room.item_description = item_description;
	tmp_Room.starting_price = starting_price;
	tmp_Room.buy_immediately_price = buy_immediately_price;
	if (tmp_Room.starting_price > 0 && tmp_Room.buy_immediately_price > tmp_Room.starting_price) {
		id_count++;
		tmp_Room.room_id = id_count;
		rooms.push_back(tmp_Room);
		return SUCCESS_CREATE_ROOM;
	}
	else return INVALID_INFORMATION;
}
