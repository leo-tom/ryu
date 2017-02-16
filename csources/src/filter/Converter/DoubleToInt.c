void DoubleToIntConverter(struct order *order){
    int i;
    for(i=1;i<=8;i++){
        pse_stream *in = get_read_Stream(order,1);
        pse_stream *out = get_write_Stream(order,1);
        if(in!=NULL||out!=NULL){
            double val;
            while((val=read_double(in))){
                write_int(out,(int)val);
            }
        }
    }
}
