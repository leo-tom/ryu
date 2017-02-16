/*
 * starter.c
 *
 *  Created on: Jul 27, 2016
 *      Author: leo
 *  This is example
 */

int start_ryu_/*[projectname]*/(ryu_info *info){
    struct begin_data_ryu data;
    struct order *orders[] = {
        NULL
    };
    pse_stream *streams[] = {
        NULL
    };
    void *funcs[]{
    	(void *)NULL
    }
    data.orders = orders;
    data.streams = streams;
    data.funcs = funcs;
    data.orderCou = 1/*[cou]*/;
    data.streamCou = 1/*[cou]*/;
    RYU_ARGV = info->argv;
    RYU_ARGC = info->argc;
    _ryu_malloc_initialize();
    initAll_ryu(&data);
    startAll(&data);
    startGC(&data);
    return 0;
}
