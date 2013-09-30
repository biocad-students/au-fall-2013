// LongestCommonSubsequence.cpp: определяет точку входа для консольного приложения.
//

#include <fstream>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>
#include <map>
#include <iterator>
#include <algorithm>
	
	std::vector<char> &split(const std::string &s, char delim, std::vector<char> &elems) 
	{
		std::stringstream ss(s);
		std::string item;
		while (std::getline(ss, item, delim)) 
		{
			if(item[0]!='\0')
			{
				elems.push_back(item[0]);
			}
		}
			return elems;
	}


	std::vector<char> split(const std::string &s, char delim) 
	{
		std::vector<char> elems;
		split(s, delim, elems);
		return elems;
	}

	int main()
	{
		std::map<std::pair<char, char>, int> blossum;
		std::ifstream matrix("matrix.txt");
		std::ifstream file("input.txt");
		std::ofstream outfile("output.txt");
		std::string str;
		do
		{
			std::getline(matrix, str);
		}
		while(str[0]=='#');
		std::vector<char> supmas = split(str, ' '); 
		for(int i = 0; i<supmas.size(); ++i)
		{
			char ch;
			matrix>>ch;
			for(int j = 0; j<supmas.size(); ++j)
			{
				int z;
				matrix>>z;
				blossum[std::make_pair(supmas[i], supmas[j])]=z;
			}
		}

		std::string a;
		std::string b;
		std::string tmp;
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

		std::vector<std::vector<int>> mas(std::max(a.length(), b.length())+1, std::vector<int>(std::max(a.length(), b.length())+1, 0));
		int gaptax = -5;
		for(int i = 1; i <= std::max(a.length(), b.length()); ++i)
		{
			mas[0][i]=gaptax*i;
			mas[i][0]=gaptax*i;
		}

		for(int i = 1; i <=a.length(); ++i)
		{
			for(int j =1; j<=b.length(); ++j)
			{
				mas[i][j]= std::max(mas[i-1][j]+gaptax, std::max(mas[i][j-1]+gaptax, mas[i-1][j-1]+blossum[std::make_pair(a[i-1], b[j-1])]));
			}
		}
		
		std::vector<char> answer(0);
		std::vector<char> answer2(0);
		int indexx = a.length();
		int indexy = b.length();
		while(indexx!=0&&indexy!=0)
		{
			if(indexy != 0 && mas[indexx][indexy] == mas[indexx][indexy - 1]+gaptax)
			{
				answer2.push_back(b[indexy-1]);
				answer.push_back('-');
				indexy--;
			}
			else
			if(indexx != 0 && mas[indexx][indexy] == mas[indexx - 1][indexy] + gaptax)
			{
				answer.push_back(a[indexx-1]);
				answer2.push_back('-');
				indexx--;
			}
			else
				if(mas[indexx][indexy] == mas[indexx - 1][indexy - 1] + blossum[std::make_pair(a[indexx - 1], b[indexy - 1])])
			{ 
				answer.push_back(a[indexx-1]);
				answer2.push_back(b[indexy-1]);
				indexx--;
				indexy--;
			}
		}
		std::cout<<mas[a.length()][b.length()]<<std::endl;
		for(int i = answer.size(); i>0; --i)
		{
			std::cout<<answer[i-1];
		}
		std::cout<<std::endl;
		for(int i = answer2.size(); i>0; --i)
		{
			std::cout<<answer2[i-1];
		}

		return 0;
	}

