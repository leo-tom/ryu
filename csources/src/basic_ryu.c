#ifndef __BASIC_RYU__
#define __BASIC_RYU__
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <string.h>
#include <unistd.h>
struct pse_st_chain * chain_initializer(){
    struct pse_st_chain *chain = (struct pse_st_chain *)malloc(sizeof(struct pse_st_chain));
    if(chain ==NULL) return NULL;
    chain->data = malloc(CHAIN_BLOCK_SIZE);
    if(chain->data == NULL) return NULL;
    chain->next = NULL;
    return chain;
}
struct order * order_Init(void *func_ptr){
    struct order *order = (struct order *)malloc(sizeof(struct order));
    if(order == NULL) return NULL;
    order->func_ptr = func_ptr;
    order->hasDone = 0x00;
    int i = 0;
    for(i=0;i<8;i++){
        order->st_write[i]= NULL;
        order->st_read[i] = NULL;
    }
    return order;
}
pse_stream * stream_Initializer(){
    pse_stream *pointer = (pse_stream *)malloc(sizeof(pse_stream));
    if(pointer == NULL) return NULL;
    pthread_mutex_init(&pointer->mutex,NULL);
    pointer->appendBlock = 0;
    pointer->appendIndex = 0;
    pointer->readBlock = 0;
    pointer->readIndex = 0;
    pointer->writing_done = 0;
    pointer->head = chain_initializer();
    if(pointer->head==NULL) return NULL;
    return pointer;
}
int appendBlock_ryu(pse_stream *stream){
    if(stream==NULL) return ERROR_RYU_INVALID_ARGS;
    struct pse_st_chain *chain = stream->head;
    while(chain->next!=NULL){ chain = chain->next; }
    if((chain->next = chain_initializer())==NULL)
        return ERROR_RYU_MALLOC_FAIL;
    return 0;
}
/*you must access to stream through read_pse_stream or write_pse_stream*/
int write_pse_stream(pse_stream *stream,char write){
    if(stream==NULL) return ERROR_RYU_INVALID_ARGS;
    if(!Is_st_writing(stream)) return ERROR_RYU_STREAM_CLOSED;
    if(!Is_st_reading(stream)) return 0;
    /*if no one is reading why do i have to write*/
    int i,apIndex;
    struct pse_st_chain *chain;
    pthread_mutex_lock(&stream->mutex);
    chain = stream->head;
    apIndex = stream->appendIndex;
    for(i=0;i<stream->appendBlock;i++)
        chain = chain->next;
    if(apIndex<CHAIN_BLOCK_SIZE){
    	/*You can just write*/
        chain->data[apIndex] = write;
        stream->appendIndex++;
    }else if(chain->next==NULL){
    	/*You have to allocate space*/
        int errorno;
        if((errorno = appendBlock_ryu(stream))){
            pthread_mutex_unlock(&stream->mutex);
            return errorno;
        }
        chain = chain->next;
        chain->data[0] = write;
        stream->appendIndex = 1;
        stream->appendBlock++;
    }else{
    	/*someone allocated already*/
    	chain = chain->next;
    	chain->data[0] = write;
    	stream->appendIndex = 1;
    	stream->appendBlock++;
    }
    pthread_mutex_unlock(&stream->mutex);
    return 0;
}
int pse_stream_end_write(pse_stream *stream){
    if(stream==NULL) return 0;
    stream->writing_done = stream->writing_done | 0x0f;
    return 0;
}int pse_stream_end_read(pse_stream *stream){
    if(stream==NULL) return 0;
    stream->writing_done = stream->writing_done | 0xf0; return 0;
}
int Is_st_reading(pse_stream *stream){
    if(stream==NULL) return 0;
    return !(stream->writing_done & 0xf0);
}
int Is_st_writing(pse_stream *stream){
    if(stream==NULL) return 0;
    return !(stream->writing_done & 0x0f);
}
/*you must access to stream through read_pse_stream or write_pse_stream*/
int read_pse_stream(pse_stream *stream){
    if(stream==NULL) return EOF;
    if(!Is_st_reading(stream)) return EOF;
    int i;
    int apb,api,rdb,rdi;
    int read_I,read_B;
    struct pse_st_chain *chain;
    while (1) {
        pthread_mutex_lock(&stream->mutex);
        apb = stream->appendBlock;
        api = stream->appendIndex;
        rdb = stream->readBlock;
        rdi = stream->readIndex;
        pthread_mutex_unlock(&stream->mutex);
        if(apb >= rdb){
            if(apb == rdb){
                if(rdi < api ){
                    goto can_read;
                }else{
                    if(stream->writing_done){
                        goto end_of_file;
                    }else{
                        /*wait*/
                        usleep(WAIT_TIME_RYU);
                        if(!Is_st_reading(stream))return EOF;
                    }
                }
            }else{
                goto can_read;
            }
        }else{
            /*Should not be happening*/
            goto end_of_file;
        }
    }

    can_read :
    i=0;
	pthread_mutex_lock(&stream->mutex);
	chain = stream->head;
	read_B = stream->readBlock;
	read_I = stream->readIndex;
	if(read_I < CHAIN_BLOCK_SIZE){
	    stream->readIndex++;
	}else{
		stream->readIndex = 1;
		stream->readBlock++;
		read_B++;
		read_I = 0;
	}
    for(;i<read_B;i++){
        chain = chain->next;
    }
    i = chain->data[read_I];
    pthread_mutex_unlock(&stream->mutex);
    return i;

    end_of_file : /*end of pseudo stream*/
        /*writing_done = writing_done | 0xf0 means reading is done*/
        pse_stream_end_read(stream);
        return EOF;
}
/*does contain \n*/
 int read_ln_ryu(pse_stream *stream, char *buff,size_t size){
     if(stream==NULL||buff==NULL) return EOF;
     if(!Is_st_reading(stream)) return EOF;
     int i = 0; int c;
     for(;i<size;i++){
         if((c = read_pse_stream(stream))== '\n' || c == EOF){
             if(c==EOF&&i==0){
                 return EOF;
             }else{
                 if(c==EOF){
                     break;
                 }else{
                     buff[i++] = c;
                     break;
                 }
             }
         }else{
             buff[i] = (char)c;
         }
     }
     buff[i] = '\0';
     return i;
}
int order_done(struct order *order){
    if(order==NULL) return ERROR_RYU_INVALID_ARGS;
    order->hasDone = 1;
    int i;
    for(i=0;i<8;i++){
        if(order->st_read[i]!=NULL)
        pse_stream_end_read(order->st_read[i]);
    }
    for(i=0;i<8;i++){
        if(order->st_write[i]!=NULL)
            pse_stream_end_write(order->st_write[i]);
    }
    return 0;
}
pse_stream * get_read_Stream(struct order *order,int th){
    if(order==NULL||th<0||th>8) return NULL;
    int i = 0;
    int n = 0;
    for (;i<8;i++ ){
        if(order->st_read[i]!=NULL){
            n++;
            if(n==th){
                return order->st_read[i];
            }
        }
    }
    return NULL;
}
pse_stream * get_read_Stream_back(struct order *order,int th){
    if(order==NULL||th<0||th>8) return NULL;
    int i = 7; int n= 0;
    for (;i>=0;i-- ) {
        if(order->st_read[i]!=NULL){
            n++;
            if(n==th){
                return order->st_read[i];
            }
        }
    }
    return NULL;
}pse_stream * get_write_Stream(struct order *order,int th){
    if(order==NULL||th<0||th>8) return NULL;
    int i = 0; int n = 0;
    for (;i<8;i++){
        if(order->st_write[i]!=NULL){
            n++;
            if(n==th){
                return order->st_write[i];
            }
        }
    }
    return NULL;
}pse_stream * get_write_Stream_back(struct order *order,int th){
    if(order==NULL||th<0||th>8) return NULL;
    int i = 7; int n = 0;
    for (;i>=0;i-- ) {
        if(order->st_write[i]!=NULL){
            n++;
            if(n==th){
                return order->st_write[i];
            }
        }
    }
    return NULL;
}
int write_str_to_stream(pse_stream *stream,const char *str){
    if(stream==NULL||str==NULL) return ERROR_RYU_INVALID_ARGS;
    int i =0; int ret;
    while(str[i]){
        if((ret=write_pse_stream(stream,str[i++]))){
            return ret;
        }
    }
    return 0;
}
/*return size of un used memory that have beed collected by GC with kilo bytes*/
int GC_pse_stream(pse_stream *stream){
    if(stream==NULL) return 0;
    int gap;
    if(stream->readBlock>0){
        /*there is block that can be collected*/
        pthread_mutex_lock(&stream->mutex);
        gap = stream->readBlock;
        struct pse_st_chain *chain = stream->head;
        struct pse_st_chain *tmp = NULL;
        int gaptmp = gap;
        while(gap>0){
            tmp = chain;
            chain = chain->next;
            free(tmp->data);
            free(tmp);
            gap--;
        }
        stream->head = chain;
        stream->readBlock -= gaptmp;
        stream->appendBlock -= gaptmp;
        pthread_mutex_unlock(&stream->mutex);
        return gaptmp*(CHAIN_BLOCK_SIZE/1024);
    }else if(!Is_st_reading(stream)){
        /*If no one reading this stream why not free all*/
        /*no need for locking mutex*/
        struct pse_st_chain *chain = stream->head;
        stream->head = NULL;
        struct pse_st_chain *temp = NULL;
        while(chain!=NULL){
            temp = chain->next;
            free(chain->data);
            free(chain);
            chain = temp;
        }
    }else{
        return 0;
    }
    return 0;
}
int stAlocate_ryu(pse_stream *stream){
    if(stream==NULL)return -1;
    int n = 0;
    struct pse_st_chain *chain = stream->head;
    while(chain!=NULL){
        chain = chain->next;
        n++;
    }
    if(stream->appendBlock+2>=n){
        pthread_mutex_lock(&(stream->mutex));
        appendBlock_ryu(stream);
        pthread_mutex_unlock(&(stream->mutex));
        /*fprintf(stderr, "Back ground worker appended\n");*/
    }
    return 0;
}
ryu_info * ryu_info_init(ryu_info *ptr){
    if(ptr!=NULL){
        int n;
        char *p = (char *)ptr;
        for(n=0;n<sizeof(ryu_info);n++){
            *p++ = '\0';
        }
    }else{
        return (ryu_info *)calloc(1,sizeof(ryu_info));
    }
    return ptr;
}
/*This will be used for removing \n*/
void replace_ryu(char *str,char from,char to){
    if(str==NULL) return;
    while(*str){
        if(*str==from){
            *str = to;
        }
        str++;
    }
}
/*read series*/
int read_char(pse_stream *stream){
    return read_pse_stream(stream);
}
int read_int(pse_stream *stream){
    char buff[sizeof(int)];
    int i = 0;
    for(;i<sizeof(int);i++){
        buff[i] = read_pse_stream(stream);
        if(!Is_st_reading(stream)){
            return 0;
        }
    }
    int *ptr = (int *)buff;
    return *ptr;
}
long read_long(pse_stream *stream){
    char buff[sizeof(long)];
    int i = 0;
    for(;i<sizeof(long);i++){
        buff[i] = read_pse_stream(stream);
        if(!Is_st_reading(stream)){
            return 0;
        }
    }
    long *ptr = (long *)buff;
    return *ptr;
}
float read_float(pse_stream *stream){
    char buff[sizeof(float)];
    int i = 0;
    for(;i<sizeof(float);i++){
        buff[i] = read_pse_stream(stream);
        if(!Is_st_reading(stream)){
            return 0.0f;
        }
    }
    float *ptr = (float *)buff;
    return *ptr;
}
double read_double(pse_stream *stream){
    char buff[sizeof(double)];
    int i = 0;
    for(;i<sizeof(double);i++){
        buff[i] = read_pse_stream(stream);
        if(!Is_st_reading(stream)){
            return 0.0f;
        }
    }
    double *ptr = (double *)buff;
    return *ptr;
}
void * read_ptr(pse_stream *stream){
    char buff[sizeof(void *)];
    int i =0;
    int c;
    for(;i<sizeof(void *);i++){
        if((c=read_pse_stream(stream))==EOF){
            return NULL;
        }
        buff[i] = c;
        if(!Is_st_reading(stream)){
            return NULL;
        }
    }
    void **ptr = (void **)buff;
    return *ptr;
}
/*without \n*/
char * read_line(struct order *order,pse_stream *stream){
	if(order==NULL||stream==NULL||!Is_st_reading(stream)){
		return NULL;
	}
	char *buff = ryumalloc(order,128);
	char *ptr = buff;
	int c,cou,kbCou;
	cou = 0;
	/*Used to allocate 1024 bytes but I think its too much so Im changing it to 128bytes*/
	kbCou = 1;
	while((c=read_pse_stream(stream))!=EOF){
		if(c=='\n'){
			*ptr = '\0';
			return buff;
		}else{
			*ptr = c;
			ptr++;
		}
		cou++;
		if(cou>=128){
			kbCou++;
			cou= 0;
			char *biggerPtr = ryumalloc(order,kbCou*128);
			if(biggerPtr==NULL){
				return NULL;
			}
			memcpy(biggerPtr,buff,(kbCou-1)*128);
			buff = biggerPtr;
			ptr = &biggerPtr[(kbCou-1)*128];
		}
	}
	if(ptr!=buff){
		*ptr = '\0';
		return buff;
	}
	return NULL;
}
/*write series*/
int write_char(pse_stream *stream,char c){
    return write_pse_stream(stream,c);
}
int write_int(pse_stream *stream,int val){
    char *ptr = (char *)&val;
    int i = 0;
    int retval;
    for(;i<sizeof(int);i++){
        if((retval=write_pse_stream(stream,ptr[i]))){
            return retval;
        }
    }
    return 0;
}
int write_long(pse_stream *stream,long val){
    char *ptr = (char *)&val;
    int i = 0;
    int retval;
    for(;i<sizeof(long);i++){
        if((retval=write_pse_stream(stream,ptr[i]))){
            return retval;
        }
    }
    return 0;
}
int write_float(pse_stream *stream,float val){
    char *ptr = (char *)&val;
    int i = 0;
    int retval;
    for(;i<sizeof(float);i++){
        if((retval=write_pse_stream(stream,ptr[i]))){
            return retval;
        }
    }
    return 0;
}
int write_double(pse_stream *stream,double val){
    char *ptr = (char *)&val;
    int i = 0;
    int retval;
    for(;i<sizeof(double);i++){
        if((retval=write_pse_stream(stream,ptr[i]))){
            return retval;
        }
    }
    return 0;
}
int write_ptr(pse_stream *stream,void *val){
    char *ptr = (char *)&val;
    int i = 0;
    int retval;
    for(;i<sizeof(void *);i++){
        if((retval=write_pse_stream(stream,ptr[i]))){
            return retval;
        }
    }
    return 0;
}
pse_stream * get_data_from_ryu_info(ryu_info *info,int th){
    if(th<=0||th>8||info==NULL){
        return NULL;
    }
    struct order *got = get_order(info);
    got->st_read[th-1] = stream_Initializer();
    return got->st_read[th-1];
}
pse_stream * write_data_to_ryu_info(ryu_info *info,int th){
    if(th<=0||th>8||info==NULL){
        return NULL;
    }
    struct order *got = get_order(info);
    got->st_write[th-1] = stream_Initializer();
    return got->st_write[th-1];
}
void streamDispose(pse_stream *st){
    pse_stream_end_read(st);
    pse_stream_end_write(st);
    GC_pse_stream(st);
    free(st);
}
#endif
