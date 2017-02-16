#include "ryu.h"
void LongToHexStrConverter(struct order *order){
	char buff[128];
	int i;
	for(i=1;i<=8;i++){
		pse_stream *in = get_read_Stream(order,i);
		pse_stream *out = get_write_Stream(order,i);
		if(in!=NULL&&out!=NULL){
			long val;
			while((val=read_long(in))){
				sprintf(buff,"%lx\n",val);
				write_str_to_stream(out,buff);
			}
		}else{
			return;
		}
	}
}
