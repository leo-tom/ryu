void Junction_ryu_entry(struct order *order){
    pse_stream *firstInput = get_read_Stream(order,1);
    pse_stream *secondInput = get_read_Stream(order,2);
    pse_stream *flowingData = get_read_Stream(order,3);
    pse_stream *firstOutput = get_write_Stream(order,1);
    pse_stream *secondOutput = get_write_Stream(order,2);
    if(firstOutput==NULL||secondOutput==NULL
            ||firstInput==NULL||secondInput==NULL||flowingData==NULL){
                fprintf(stderr, "Junction filter got too few streams\n");
                return;
            }
    int c;
    c = read_pse_stream(firstInput);
    if(c!=EOF){
        while((c=read_pse_stream(flowingData))!=EOF){
            write_pse_stream(firstOutput,c);
        }
        return;
    }
    c = read_pse_stream(secondInput);
    if(c!=EOF){
        while((c=read_pse_stream(flowingData))!=EOF){
            write_pse_stream(secondOutput,c);
        }
        return;
    }
    /*Both input streams were false*/
}
