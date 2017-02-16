#include <stdio.h>
void wc_ryu(struct order *order){
    pse_stream *read = get_read_Stream(order,1);
    pse_stream *gothrough = get_write_Stream(order,1);
    pse_stream *writeTo = get_write_Stream_back(order,1);
    if(writeTo==gothrough){
    	gothrough = NULL;
    }
    if(writeTo==NULL||read==NULL){
    	/*fprintf(stderr, "stream is not given [I amd wc_ryu]\n");*/
    	return;
    }
    int c;
    unsigned long counter = 0;
    char buff[128];
    while((c=read_pse_stream(read))!=EOF){
    	counter++;
    	if(gothrough!=NULL){
    		write_pse_stream(gothrough,c);
    	}
    }
    sprintf(buff,"%lu\n",counter);
    write_str_to_stream(writeTo,buff);
}
