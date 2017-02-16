#include <stdio.h>
void duplicate_ryu(struct order *order){
	pse_stream *writeTo[8] = {NULL};
	pse_stream *read = get_read_Stream(order,1);
	if(read==NULL){
		/*fprintf(stderr, "There is no stream that I can read[I am duplicate_ryu]\n");*/
		return;
	}
	int i = 0;
	int n = 1;
	int c;
	pse_stream *tmp;
	while((tmp=get_write_Stream(order,n++))!=NULL){
		writeTo[i++] = tmp;
	}
	if(i==0){
		/*non stream was given*/
		return;
	}
	while((c=read_pse_stream(read))!=EOF){
		for(i=0;writeTo[i]!=NULL;i++){
			write_pse_stream(writeTo[i],c);
		}
	}
}
