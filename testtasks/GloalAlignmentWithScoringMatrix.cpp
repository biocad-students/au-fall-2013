// LongestCommonSubsequence.cpp: определяет точку входа для консольного приложения.
//

#include <fstream>
#include <iostream>
#include <string>
#include <vector>
#include <map>
using std::pair;
using std::cin;
using std::cout;
using std::string;
using std::endl;
using std::max;
using std::vector;
using std::map;
using std::make_pair;
int mas[1010][1010] = {};
map<pair<char, char>, int> blossum;

string supmas = "ACDEFGHIKLMNPQRSTVWY";

	int main()
	{
		std::ifstream file("input.txt");
		std::ofstream outfile("output.txt");
		std::string str;
		std::getline( file, str );

		for(int i = 0; i<20; i++)
		{
			char ch;
			file>>ch;
			for(int j = 0; j<20; j++)
			{
				int z;
				file>>z;
				blossum[make_pair(supmas[i], supmas[j])]=z;
			}
		}

		string a;
		string b;
		string tmp;
		file>>tmp;

		while(file>>tmp)
		{
			if(tmp[0]=='>')
				break;
			a+=tmp;
		}
	
		while(file>>tmp)
		{
			b+=tmp;
		}

		int gaptax = -5;
		for(int i = 1; i <=max(a.length(), b.length()); i++)
		{
			mas[0][i]=gaptax*i;
			mas[i][0]=gaptax*i;
		}

		for(int i = 1; i <=a.length(); i++)
		{
			for(int j =1; j<=b.length(); j++)
			{
				mas[i][j]=max(mas[i-1][j]+gaptax, max(mas[i][j-1]+gaptax, mas[i-1][j-1]+blossum[make_pair(a[i-1], b[j-1])]));
			}
		}
		
		vector<char> answer(0);
		vector<char> answer2(0);
		int indexx = a.length();
		int indexy = b.length();
		while(indexx!=0&&indexy!=0)
		{
			if(indexy!=0&&mas[indexx][indexy]==mas[indexx][indexy-1]+gaptax)
			{
				answer2.push_back(b[indexy-1]);
				answer.push_back('-');
				indexy--;
			}
			else
			if(indexx!=0&&mas[indexx][indexy]==mas[indexx-1][indexy]+gaptax)
			{
				answer.push_back(a[indexx-1]);
				answer2.push_back('-');
				indexx--;
			}
			else
				if(mas[indexx][indexy]==mas[indexx-1][indexy-1]+blossum[make_pair(a[indexx-1], b[indexy-1])])
			{
				answer.push_back(a[indexx-1]);
				answer2.push_back(b[indexy-1]);
				indexx--;
				indexy--;
			}
		}
		cout<<mas[a.length()][b.length()]<<endl;
		for(int i = answer.size(); i>0; --i)
		{
			cout<<answer[i-1];
		}
		cout<<endl;
		for(int i = answer2.size(); i>0; --i)
		{
			cout<<answer2[i-1];
		}

		return 0;
	}

