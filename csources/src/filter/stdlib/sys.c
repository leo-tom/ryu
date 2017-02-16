#include <sys/types.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdio.h>
#include <stropts.h>
struct ryu_sys_data{
	FILE *fp;
	pse_stream *out;
};
void * Sys_ryu_readData(void *arg){
	struct ryu_sys_data *data = (struct ryu_sys_data *)arg;
	pse_stream *out = data->out;
	FILE *in = data->fp;
	int c;
	while((c=fgetc(in))!=EOF){
		write_pse_stream(out,c);
	}
	return NULL;
}
void Sys_ryu_Entry(struct order *order){
	pse_stream *in = get_read_Stream(order,1);
	pse_stream *cmdSt = get_read_Stream_back(order,1);
	if(in==cmdSt)in = NULL;
	if(cmdSt==NULL){
		/*No cmd is given*/
		return;
	}
	pse_stream *out = get_write_Stream(order,1);
	FILE *input = NULL;
	char buff[1024];
	if(in!=NULL){
		umask(S_IROTH | S_IWOTH);
		sprintf(buff,"/tmp/pipefile_ryu_%d_%p",getpid(),order);
		if(mkfifo(buff,S_IRUSR | S_IWUSR | S_IRGRP | S_IWGRP )){
			return;
		}
	}
	char cmd[2048];
	if(in!=NULL){
		sprintf(cmd,"%s < %s",read_line(order,cmdSt),buff);
	}else{
		sprintf(cmd,"%s",read_line(order,cmdSt));
	}
	FILE *readF = NULL;
	if((readF=popen(cmd,"r"))==NULL){
		fprintf(stderr,"Failed to make app");
	}
	/*Fifo file cannot be opend if no one is reading*/
	if(in!=NULL){
		input = fopen(buff,"w");
		if(input==NULL){
			fprintf(stderr,"cant open\n");
			return;
		}
	}
	int c;
	if(in!=NULL){
		pthread_t thread;
		struct ryu_sys_data *data = (struct ryu_sys_data *)ryumalloc(order,sizeof(struct ryu_sys_data));
		data->fp = readF;
		data->out = out;
		if(pthread_create(&thread,NULL,Sys_ryu_readData,(void *)data)){
			fprintf(stderr,"Could not make new thread");
		}
		while((c=read_pse_stream(in))!=EOF){
			fputc(c,input);
		}
		fclose(input);
		pthread_join(thread,NULL);
	}else{
		while((c=fgetc(readF))!=EOF){
			write_pse_stream(out,c);
		}
	}
	if(readF!=NULL) pclose(readF);
	remove(buff);
}

