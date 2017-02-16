void DataReader_Entry(struct order *order){
    int i = 1;
    struct order *Sorder = &(((ryu_info *)order->info)->order);
    for(;i<=8;i++){
        pse_stream *in = get_write_Stream(Sorder,i); /*Because you have to read from input stream*/
        pse_stream *out = get_write_Stream(order,i);
        if(in==NULL||out==NULL){
            break;
        }else{
            int c;
            while((c=read_pse_stream(in))!=EOF){
                write_pse_stream(out,c);
            }
            pse_stream_end_read(in);
        }
    }
}
