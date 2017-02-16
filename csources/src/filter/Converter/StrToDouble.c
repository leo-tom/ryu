#include "ryu.h"
void StrToDoubleConverter_ryu(struct order *order){
	int i = 0;
	char buff[1024];
	for(i=1;i<=8;i++){
		pse_stream *in = get_read_Stream(order,i);
		pse_stream *out = get_write_Stream(order,i);
		if(in!=NULL&&out!=NULL){
			while(read_ln_ryu(in,buff,1024)>0){
				write_double(out,atof(buff));
			}
		}else{
			return;
		}
	}
}
