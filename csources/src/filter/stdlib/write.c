#include <stdio.h>
void write_ryu(struct order *order){
	pse_stream *in = get_read_Stream(order,1);
	pse_stream *filenameS = get_read_Stream_back(order,1);
	int stout = 0;
	int sterr = 0;
	if(in==filenameS){
		stout = 1;
		filenameS = NULL;
	}
	char *buff;
	FILE *fp = NULL;
	if(filenameS!=NULL){
		buff = read_line(order,filenameS);
		if(!strcmp(buff,"stderr")||!strcmp(buff,"standard error")){
			sterr = 1;
		}else if(!strcmp(buff,"stdout")||!strcmp(buff,"standard output")){
			stout = 1;
		}else{
			if((fp=fopen(buff,"w"))==NULL){
				/*fprintf(stderr,"%s could not be opened",filenameS);*/
				return;
			}
		}
	}
	if(sterr){
		fp = stderr;
	}else if(stout){
		fp = stdout;
	}
	int c;
	while((c=read_pse_stream(in))!=EOF){
		fputc(c,fp);
	}
	if(!sterr&&!stout)
		fclose(fp);
}
