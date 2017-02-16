#ifndef __STATIC_RYU__
#define __STATIC_RYU__
int initAll_ryu(struct begin_data_ryu *data){
    int i = 0;
    for(i=0;i<data->streamCou;i++){
        if((data->streams[i] = stream_Initializer())==NULL) return ERROR_RYU_MALLOC_FAIL;
    }
    for(i=0;i<data->orderCou;i++){
        data->orders[i] = order_Init(data->funcs[i]);
    }
    return 0;
}
void startAll(struct begin_data_ryu *data){
    int i = 0;
    for(;i<data->orderCou;i++){
        pthread_t thread;
        pthread_create(&thread,NULL,data->orders[i]->func_ptr,(void *)data->orders[i]);
    }
}
void startGC(struct begin_data_ryu *data){
    int i = 0;
    int freedKB = 0;
    int alive_orders;
    while(1){
        for(i=0;i<data->streamCou;i++){
            if((freedKB = GC_pse_stream(data->streams[i]))!=0){
                /*fprintf(stderr, "GC collected %d\n", freedKB);*/
            }
            if(data->streams[i]->head!=NULL){
                stAlocate_ryu(data->streams[i]);
            }
        }
        alive_orders = 0;
        for(i=0;i<data->orderCou;i++){
            if(!(data->orders[i]->hasDone)){
                alive_orders++;
            }
        }
        if(!alive_orders){
            /*end*/
            for(i=0;i<data->orderCou;i++){
                free(data->orders[i]);
            }
            for(i=0;i<data->streamCou;i++){
                free(data->streams[i]);
            }
            return;
        }
        usleep(WAIT_TIME_RYU);
    }
}
#endif
