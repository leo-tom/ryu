#include "ryu.h"
void DoubleToScinencetrConverter(struct order *order){
	char buff[128];
	int i;
	for(i=1;i<=8;i++){
		pse_stream *in = get_read_Stream(order,i);
		pse_stream *out = get_write_Stream(order,i);
		if(in!=NULL&&out!=NULL){
			double val;
			while((val=read_double(in))){
				sprintf(buff,"%le\n",val);
				write_str_to_stream(out,buff);
			}
		}else{
			return;
		}
	}
}
