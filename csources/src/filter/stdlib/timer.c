#include <stdio.h>
void timer_ryu(struct order *order){
    pse_stream *in = get_read_Stream(order,1);
    pse_stream *out = get_write_Stream(order,1);
    pse_stream *result = get_write_Stream_back(order,1);
    if(in==NULL||out==NULL)return;
    if(out==result)result = NULL;
    char buff[1024];
    clock_t start, end;
    int c;
    if(in == NULL)
    	start = clock();
    else{
    	c = read_pse_stream(in);
    	start = clock();
    	/*in could be closed at beginning*/
        if(c!=EOF)
            write_pse_stream(out,c);
        else
            pse_stream_end_write(out);	/*I was not used*/
    }
    while((c=read_pse_stream(in))!=EOF){
        write_pse_stream(out,c);
    }
    end = clock();
    /*val = difftime(end,start);*/
    sprintf(buff,"%d",end - start);
    write_str_to_stream(result,buff);
}
