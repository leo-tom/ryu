void _argsRyuEntry(struct order *order){
    ryu_info *info = (ryu_info *)order->info;
    if(info!=NULL){
        int argc = info->argc;
        char **argv = info->argv;
        if(argv!=NULL){
            int i;
            for(i=1;i<=8;i++){
                pse_stream *out = get_write_Stream(order,i);
                if(out!=NULL && argc>i && argv[i]!=NULL){
                    write_str_to_stream(out,argv[i]);
                }else{
                    break;
                }
            }
        }
    }
}
