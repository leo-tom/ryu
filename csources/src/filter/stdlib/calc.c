#include <stdio.h>
double calc_ryu_inside(char *formula){
	#define PI_RYU (3.141592)
	const double PLUS = ((double)'+')+((double)( ((double)'+') / 100.0f));
	const double MINUS = ((double)'-')+((double)( ((double)'-') / 100.0f));
	const double MULTI = ((double)'*')+((double)( ((double)'*') / 100.0f));
	const double DIVIDE = ((double)'/')+((double)( ((double)'/') / 100.0f));
	const double SQUARE = ((double)'^')+((double)( ((double)'^') / 100.0f));
	int i;
	int n=0;
	int cou;
	double containmul[30];
	char memo[100];
	double plusisleft[30];
	int plusisleftc=0;
	int a=0;
	int tmpi;
	int e=0;
	cou=0;
	for(i=0;formula[i]!='\0';i++){
		if(isdigit(formula[i])){
			cou = 0;
			while(isdigit(memo[cou]=formula[i])||memo[cou]=='.'){
				i++;
				cou++;
			}
			memo[cou] = '\0';
			i--;
			containmul[a++]=atof(memo);
		}else if(formula[i] == '+'){
			containmul[a++] = PLUS;
		}else if(formula[i] == '-'){
			containmul[a++] = MINUS;
		}else if(formula[i] =='^'){
			containmul[a++] = SQUARE;
		}else if(formula[i] == '/'){
			containmul[a++] = DIVIDE;
		}else if(formula[i]=='*'){
			containmul[a++] = MULTI;
		}else if(formula[i]=='('){
			cou=1;
			n=0;
			i++;
			for(;;i++,n++){
				if(formula[i]=='('){
					cou++;
				}else if(formula[i]==')'){
					cou--;
				}
				if(cou==0){
					memo[n]='\0';
					for(e=0;e<=n;e++){
						formula[e]=memo[e];
					}
					containmul[a++]=calc_ryu_inside(formula);
					break;
				}
				memo[n]=formula[i];
			}
		}else if(formula[i]=='s'||formula[i+1]=='i'||formula[i+2]=='n'){
			containmul[a++]='S';
			containmul[a++]='I';
			i+=2;
		}else if(formula[i]=='c'||formula[i+1]=='o'||formula[i+2]=='s'){
			containmul[a++]='C';
			containmul[a++]='O';
			i+=2;
		}else if(formula[i]=='t'||formula[i+1]=='a'||formula[i+2]=='n'){
			containmul[a++]='T';
			containmul[a++]='A';
			i+=2;
		}else{
			return -0.0;
		}
	}
	containmul[a] = '\0';
	a=0;
	while(containmul[a]!='\0'){
		if(containmul[a]=='S'&&containmul[a+1]=='I'){
			containmul[a]=sin(containmul[a+2]*PI_RYU/180.0f);
			a++;
			for(tmpi=a;containmul[a+2]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			containmul[a]='\0';
			a=tmpi;
		}else if(containmul[a]=='C'&&containmul[a+1]=='O'){
			containmul[a]=cos(containmul[a+2]*PI_RYU/180.0f);
			a++;
			for(tmpi=a;containmul[a+2]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			containmul[a]='\0';
			a=tmpi;
		}else if(containmul[a]=='T'&&containmul[a+1]=='A'){
			containmul[a]=tan(containmul[a+2]*PI_RYU/180.0);
			a++;
			for(tmpi=a;containmul[a+2]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			containmul[a]='\0';
			a=tmpi;
		}
		a++;
	}
	a=0;
	while(containmul[a]!='\0'){
		if(containmul[a]==SQUARE){
			if(containmul[a+1]<0){
				containmul[a-1]=(1/(powl(containmul[a-1],(-1*containmul[a+1]))));
			}else{
				containmul[a-1]=powl(containmul[a-1],containmul[a+1]);
			}
			for(tmpi=a;containmul[a]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			a=tmpi;
		}
		a++;
	}
	for(a=0;containmul[a]!='\0';a++){
		if(containmul[a]==DIVIDE){
			containmul[a-1] = containmul[a-1]/containmul[a+1];
			for(tmpi=a;containmul[a]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			a=tmpi;
		}
	}
	for(a=0;containmul[a]!='\0';a++){
		if(containmul[a]==MULTI){
			containmul[a-1] = containmul[a-1]*containmul[a+1];
			for(tmpi=a;containmul[a]!='\0';a++){
				containmul[a]=containmul[a+2];
			}
			a=tmpi;
		}
	}
	plusisleftc=0;
	for(i=0;i<30;i++){
		plusisleft[i] = 1.0f;
	}
	a=0;
	while(containmul[a]!= '\0'){
		if(containmul[a]==PLUS){
			plusisleftc++;
			a++;
		}else if(containmul[a]==MINUS){
			containmul[a+1]*=-1;
			plusisleftc++;
			a++;
		}else{
			plusisleft[plusisleftc]*=containmul[a];
			a++;
		}
	}

	plusisleft[plusisleftc+1] = '\0';
	i=0;
	double ans=0.0f;
	while(plusisleft[i]!='\0'){
		ans+=plusisleft[i++];
	}
	return ans;
}
void calc_ryu(struct order *order){
	pse_stream *out = get_write_Stream(order,1);
	pse_stream *in = get_read_Stream(order,1);
	char *buff;
	if(in==NULL||out==NULL)return;
	while((buff=read_line(order,in))!=NULL){
		write_double(out,calc_ryu_inside(buff));
		/*sprintf(buff,"%f\n",);
        write_str_to_stream(out,buff);
        */
    }
}
