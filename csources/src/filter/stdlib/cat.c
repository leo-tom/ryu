#include <stdio.h>
void cat_ryu(struct order *order){
	pse_stream *out = get_write_Stream(order,1);
	if(out==NULL){
		return;
	}
	int i,c;
	for(i=0;i<8;i++)
		if(order->st_read[i]!=NULL)
			while((c=read_pse_stream(order->st_read[i]))!=EOF)
				write_pse_stream(out,c);
}
