#ifndef __GET_OPT_RYU__
#define __GET_OPT_RYU__
/*for arg analysis*/
/*use TLS(thread local storage)*/
__thread int GET_OPT_RYU_NEXT = 0;
#include <string.h>
char * ryu_getopt(struct order *id,const char *str){
    if(str == NULL || id == NULL) return NULL;
    char buff[1024];
    char *buffp = buff;
    int i = 0;
    str += GET_OPT_RYU_NEXT;
    if(strlen(str)<=0) return NULL;
    /*if FS or '-' do not return those val*/
    while(*str==' ' || *str == '\n' || *str == '\t' || *str == '-'){
        str++;
        GET_OPT_RYU_NEXT++;
    }
    while(*str != '\0' && *str!=' ' && *str != '\n' && *str != '\t'){
        if(buffp-buff >= 1024){
            /*error*/
            return NULL;
        }
        *buffp++ = *str++;
        GET_OPT_RYU_NEXT++;
    }
    *buffp = '\0';
    char *ret = ryumalloc(id,strlen(buff));
    if(ret==NULL) return NULL;
    strcpy(ret,buff);
    return ret;
}
#endif
