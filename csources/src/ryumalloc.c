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
    char *memory;
    struct ryu_malloc_using_chain *next;
};
struct ryu_malloc_memories ryu_malloc_head;
pthread_mutex_t ryu_malloc_chain_mutex;
void _ryu_malloc_initialize(){
    pthread_mutex_init(&ryu_malloc_chain_mutex,NULL);
    ryu_malloc_head.holder = NULL;
    ryu_malloc_head.next = NULL;
    ryu_malloc_head.using = NULL; /*(struct ryu_malloc_using_chain *)malloc(sizeof(struct ryu_malloc_using_chain));*/
}
char * ryumalloc(struct order *order,size_t size){
    struct ryu_malloc_memories *current;
    struct ryu_malloc_memories *lastChain_orders;
    struct ryu_malloc_using_chain *current_using;
    struct ryu_malloc_using_chain *lastChain;
    char *val = malloc(size);
    if(val==NULL){
        return NULL;
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
    current->holder = order;
    pthread_mutex_init(&lastChain_orders->next->mutex,NULL);
    pthread_mutex_unlock(&ryu_malloc_chain_mutex);
    return val;
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
void ryufree(void *ptr){
    return;
}
#endif
