#include "ryu.h"
void PtrToStrConverter(struct order *order){
	char buff[128];
	int i;
	for(i=1;i<=8;i++){
		pse_stream *in = get_read_Stream(order,i);
		pse_stream *out = get_write_Stream(order,i);
		if(in!=NULL&&out!=NULL){
			void *val;
			while((val=read_ptr(in))){
				sprintf(buff,"%p\n",val);
				write_str_to_stream(out,buff);
			}
		}else{
			return;
		}
	}
}
