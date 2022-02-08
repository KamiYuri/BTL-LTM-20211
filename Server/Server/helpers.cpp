
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

string login(string email, string password, /*char client_ip[INET_ADDRSTRLEN], int client_port,*/ SOCKET client_socket, vector<User> users, int id_count) {
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
				id_count++;
				User tmp_user;
				tmp_user.user_id = to_string(id_count);
				tmp_user.username = email;
				tmp_user.password = password;
				tmp_user.socket = client_socket;
				//strcpy_s(tmp_user.client_ip, client_ip);
				//tmp_user.client_port = client_port;
				users.push_back(tmp_user);
				return SUCCESS_LOGIN + tmp_user.user_id;
			}
		}
		return FAILED_LOGIN;
	}
}

string logout(string user_id, vector<User> users){
	for (int i = 0; i < users.size(); i++) {
		if (users[i].user_id == user_id) {
			users.erase(users.begin()+i-1);
			return SUCCESS_LOGOUT;
		}
	}
	return FAILED_LOGOUT;
}

string show_room(vector<Room> rooms) {
	string message;
	for (int i = 0;i<rooms.size();i++) {
		message = SUCCESS_SHOW_ROOM + rooms[i].room_id + SPLITING_DELIMITER_2 + rooms[i].item_name + SPLITING_DELIMITER_2 + rooms[i].item_description + SPLITING_DELIMITER_1;
	}
	message += ENDING_DELIMITER;
	return message;
}

string join_room(string room_id, string user_id, vector<Room> rooms) {
	for (int i = 0;i<rooms.size();i++) {
		if (room_id == rooms[i].room_id) {
			rooms[i].client_list.push_back(user_id);
			return SUCCESS_JOIN_ROOM;
		}
	}
	return ROOM_NOT_FOUND;
}

string bid(int price, string room_id, string user_id, vector<Room> rooms) {
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
	return "Wrong ID";
}

string buy_immediately(string room_id, string user_id, vector<Room> rooms) {
	for (int i = 0;i<rooms.size();i++) {
		if (rooms[i].room_id == room_id) {
			if (rooms[i].owner == "-1") {
				rooms[i].owner == user_id;
				return SUCCESS_BUY_IMMEDIATELY;
			}
			else return ALREADY_SOLD;
		}
	}
	return "Wrong ID";
}

string create_room(string item_name, string item_description, int starting_price, int buy_immediately_price, vector<Room> rooms, int id_count) {
	Room tmp_room;
	tmp_room.item_name = item_name;
	tmp_room.item_description = item_description;
	tmp_room.starting_price = starting_price;
	tmp_room.buy_immediately_price = buy_immediately_price;
	if (tmp_room.starting_price > 0 && tmp_room.buy_immediately_price > tmp_room.starting_price) {
		id_count++;
		tmp_room.room_id = to_string(id_count);
		rooms.push_back(tmp_room);
		return SUCCESS_CREATE_ROOM + tmp_room.room_id;
	}
	else return INVALID_INFORMATION;
}