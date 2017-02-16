#ifndef __MALLOC_RYU__
#define __MALLOC_RYU__
#include <stdlib.h>
struct ryu_malloc_memories{
    struct order *holder;
    pthread_mutex_t mutex;
    struct ryu_malloc_memories *next;
    struct ryu_malloc_using_chain *using;
};
struct ryu_malloc_using_chain{
    size_t size;
    char *memory;
    struct ryu_malloc_using_chain *next;
};
static struct ryu_malloc_memories ryu_malloc_head;
static pthread_mutex_t ryu_malloc_chain_mutex;
static int has_Initialized_ryumalloc = 0;
void _ryu_malloc_initialize(){
    if(has_Initialized_ryumalloc){
        return;
    }
    has_Initialized_ryumalloc = 1;
    pthread_mutex_init(&ryu_malloc_chain_mutex,NULL);
    ryu_malloc_head.holder = NULL;
    ryu_malloc_head.next = NULL;
    ryu_malloc_head.using = NULL;
}
void * ryumalloc(struct order *order,size_t size){
    struct ryu_malloc_memories *current;
    struct ryu_malloc_memories *lastChain_orders;
    struct ryu_malloc_using_chain *current_using;
    struct ryu_malloc_using_chain *lastChain;
    char *val = malloc(size);
    if(val==NULL){
        return NULL;pthread_mutex_lock(&ryu_malloc_chain_mutex);
    }
    pthread_mutex_lock(&ryu_malloc_chain_mutex);
    current = &ryu_malloc_head;
    lastChain_orders = current;
    while(current!=NULL){
        if(current->holder==order){
            pthread_mutex_lock(&current->mutex);
            current_using = current->using;
            lastChain = (struct ryu_malloc_using_chain *)malloc(sizeof(struct ryu_malloc_using_chain));
            current->using = lastChain;
            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
            lastChain->next = current_using;
            lastChain->memory = val;
            lastChain->size = size;
            pthread_mutex_unlock(&current->mutex);
            return val;
        }
        lastChain_orders = current;
        current = current->next;
    }
    current = (struct ryu_malloc_memories *)malloc(sizeof(struct ryu_malloc_memories));
    lastChain_orders->next = current;
    current->next = NULL;
    current->using = (struct ryu_malloc_using_chain *)malloc(sizeof(struct ryu_malloc_using_chain));
    current->using->next = NULL;
    current->using->memory = val;
    current->using->size = size;
    current->holder = order;
    pthread_mutex_init(&lastChain_orders->next->mutex,NULL);
    pthread_mutex_unlock(&ryu_malloc_chain_mutex);
    return val;
}

void *ryucalloc(struct order *val,int howmany,size_t size){
    int totals = size*howmany;
    char *ptr = ryumalloc(val,totals);
    char *retval;
    int n = 0;
    if(ptr==NULL){
        return ptr;
    }
    retval = ptr;
    for(;n<totals;n++){
        *ptr++ = '\0';
    }
    return retval;
}

void * ryurealloc(struct order *holder,void *ptr,size_t size){
    if(ptr==NULL||size==0||holder==NULL){
        return NULL;
    }
    struct ryu_malloc_memories *current;
    struct ryu_malloc_using_chain *current_chain;
    void *tmp;
    pthread_mutex_lock(&ryu_malloc_chain_mutex);
    current = &ryu_malloc_head;
    while(current!=NULL){
        if(current->holder == holder){
            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
            pthread_mutex_lock(&(current->mutex));
            current_chain = current->using;
            while(current_chain!=NULL){
                if(current_chain->memory <= ((char *)ptr)
                    && ptr <= (void *)(current_chain->memory+current_chain->size)){
                    if((tmp=realloc(ptr,size))==NULL){
                        pthread_mutex_unlock(&(current->mutex));
                        return NULL;
                    }else{
                        current_chain->memory = tmp;
                        current_chain->size = size;
                        pthread_mutex_unlock(&(current->mutex));
                        return tmp;
                    }
                    /*code does not reach here*/
                    return ryumalloc(holder,size);
                }
                current_chain = current_chain->next;
            }
            pthread_mutex_unlock(&(current->mutex));
            return ryumalloc(holder,size);
        }
        current = current->next;
    }
    pthread_mutex_unlock(&ryu_malloc_chain_mutex);
    return ryumalloc(holder,size);
}

int ryufree_unreachable(struct order *dead_order){
    struct ryu_malloc_memories *current;
    struct ryu_malloc_memories *last;
    pthread_mutex_lock(&ryu_malloc_chain_mutex);
    current = &ryu_malloc_head;
    last = current;
    while(current!=NULL){
        if(current->holder==dead_order){
            last->next = current->next;
            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
            int n = 0;
            struct ryu_malloc_using_chain *chain_current = current->using;
            struct ryu_malloc_using_chain *tmp;
            free(current);
            while(chain_current!=NULL){
                tmp = chain_current;
                chain_current = chain_current->next;
                free(tmp->memory);
                free(tmp);
                n++;
            }
            return n;
        }
        last = current;
        current = current->next;
    }
    pthread_mutex_unlock(&ryu_malloc_chain_mutex);
    return 0;
}
/*very slow*/
void ryufree(struct order *order,void *ptr){
    struct ryu_malloc_memories *current;
    struct ryu_malloc_using_chain *last;
    struct ryu_malloc_using_chain *current_using;
    pthread_mutex_lock(&ryu_malloc_chain_mutex);
    current = &ryu_malloc_head;
    while(current!=NULL){
        if(current->holder == order){
            current_using = current->using;
            pthread_mutex_lock(&current->mutex);
            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
            last = NULL;
            while(current_using!=NULL){
                if(current_using->memory <= (char *)ptr && current_using->memory + current_using->size > (char *)ptr){
                    if(last==NULL){
                        pthread_mutex_lock(&ryu_malloc_chain_mutex);
                        if(current->using == current_using){
                            current->using = NULL;
                            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
                        }else{
                            /*I am too lazy to write this case call recursively*/
                            pthread_mutex_unlock(&ryu_malloc_chain_mutex);
                            pthread_mutex_unlock(&(current->mutex));
                            ryufree(order,ptr);
                            return;
                        }
                    }else{
                        last->next = current_using->next;
                    }
                    free(current_using->memory);
                    free(current_using);
                    goto end;
                }
                last = current_using;
                current_using = current_using->next;
            }
            end :
            pthread_mutex_unlock(&(current->mutex));
            return;
        }
        current = current->next;
    }
    return;
}
#endif
