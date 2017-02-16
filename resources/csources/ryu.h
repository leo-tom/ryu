#ifndef __RYU_H__
#define __RYU_H__
#include <pthread.h>
#include <stdlib.h>
#define CHAIN_BLOCK_SIZE (4096)
#define WAIT_TIME_RYU (5000)
struct pse_st_chain{
    char *data;
    struct pse_st_chain *next;
};
typedef struct pseudo_stream{
    pthread_mutex_t mutex;
    int appendBlock;
    int appendIndex;
    int readBlock;
    int readIndex;
    char writing_done;         /*If first 4 bits are set reading is done. If last 4 bits are set writing is done*/
    struct pse_st_chain *head;
}pse_stream;
struct order{
    void *func_ptr;
    char hasDone;			/*using it as bool*/
    void *info;
    pthread_t thread;
    pse_stream *st_read[8]; /* stream for read. there are maximum 8 of them*/
    pse_stream *st_write[8]; /*for writing. there are 8 of them*/
};
struct begin_data_ryu{
	pse_stream **streams;
	struct order **orders;
	int streamCou;
	int orderCou;
    void **funcs;
};
typedef struct _giving_data_to_ryu_starter{
	struct order order;
	pthread_t thread;
	char BackGround;
	int argc;
	char **argv;
}ryu_info;
/*
make sure to give pointers to those macros
*/
#define do_back(x) (x->BackGround = 1)
#define don_back(x) (x->BackGround = 0)
#define get_thread(x) (x->thread)
#define get_order(x) (&(x->order))
#define get_argc(x) (x->argc)
#define get_argv(x) (x->argv)
#define set_argc(x,v) (x->argc = v )
#define set_argv(x,v) (x->argv = v )
extern void streamDispose(pse_stream *st);
extern char * read_line(struct order *order,pse_stream *stream);
extern pse_stream * get_data_from_ryu_info(ryu_info *info,int th);
extern pse_stream * write_data_to_ryu_info(ryu_info *info,int th);
extern int stAlocate_ryu(pse_stream *stream);
extern ryu_info * ryu_info_init(ryu_info *ptr);
extern void * ryumalloc(struct order *order,size_t size);
extern void * ryucalloc(struct order *order,int howmany,size_t size);
extern void * ryurealloc(struct order *order,void *ptr,size_t size);
extern void ryufree(struct order *order,void *ptr);
extern int order_done(struct order *order);
extern int write_pse_stream(pse_stream *stream,char write);
extern int read_pse_stream(pse_stream *stream);
extern int read_ln_ryu(pse_stream *stream, char *buff,size_t size);
extern pse_stream * stream_Initializer();
extern struct pse_st_chain * chain_initializer();
extern struct order * order_Init(void *fuc_ptr);
extern void pse_stream_end_read(pse_stream *stream);
extern void pse_stream_end_write(pse_stream *stream);
extern int Is_st_reading(pse_stream *stream);
extern int Is_st_writing(pse_stream *stream);
extern int appendBlock_ryu(pse_stream *stream);
extern int write_str_to_stream(pse_stream *stream,const char *str);
extern int GC_pse_stream(pse_stream *stream);
extern pse_stream * get_write_Stream_back(struct order *order,int th);
extern pse_stream * get_write_Stream(struct order *order,int th);
extern pse_stream * get_read_Stream_back(struct order *order,int th);
extern pse_stream * get_read_Stream(struct order *order,int th);
extern pse_stream * get_write_stream(struct order *order,int th);
extern pse_stream * get_write_stream_back(struct order *order,int th);
extern pse_stream * get_read_stream_back(struct order *order,int th);
extern pse_stream * get_read_stream(struct order *order,int th);
extern void replace_ryu(char *str,char from,char to);
extern int write_ptr(pse_stream *stream,void *val);
extern int write_double(pse_stream *stream,double val);
extern int write_float(pse_stream *stream,float val);
extern int write_long(pse_stream *stream,long val);
extern int write_int(pse_stream *stream,int val);
extern int write_char(pse_stream *stream,char c);
extern void * read_ptr(pse_stream *stream);
extern double read_double(pse_stream *stream);
extern float read_float(pse_stream *stream);
extern long read_long(pse_stream *stream);
extern int read_int(pse_stream *stream);
extern int read_char(pse_stream *stream);
extern void startGC(struct begin_data_ryu *data);
extern void startAll(struct begin_data_ryu *data);
extern int initAll_ryu(struct begin_data_ryu *data);
extern void _ryu_malloc_initialize();
extern int ryufree_unreachable(struct order *dead_order);
#define ERROR_RYU_MALLOC_FAIL (-1)
#define ERROR_RYU_INVALID_ARGS (-2)
#define ERROR_RYU_STREAM_CLOSED (-3)
#define ERROR_RYU_UNKNOWN (-4)
#endif
