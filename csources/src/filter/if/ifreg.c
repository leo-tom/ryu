#include <regex.h>
void if_regular_expression_entry(struct order *order){
    pse_stream *regulerE = get_read_Stream_back(order,1);
    pse_stream *trueS = get_write_Stream(order,1);
    pse_stream *falseS = get_write_Stream(order,2);
    if(regulerE==NULL||trueS==NULL||falseS==NULL||trueS==falseS){
        fprintf(stderr, "Too few stream for ifreg\n");
        return;
    }
    char *reg = read_line(order,regulerE);
    regex_t regex;
    if(regcomp(&regex,reg,0)){
        fprintf(stderr, "Failed to compile regular expression %s\n",reg );
        return;
    }
    int i = 1;
    char *buff = ryumalloc(order,4096);
    int result;
    if(buff==NULL){
        fprintf(stderr, "Failed to allocate memory\n");
        return;
    }
    for(;i<=8;i++){
        pse_stream *in = get_read_Stream(order,i);
        if(in==regulerE){
            break;
        }
        while(read_ln_ryu(in,buff,4096)>0){
            if(!(result=regexec(&regex,buff,0,NULL,0))){
                /*match*/
                write_str_to_stream(trueS,buff);
            }else if(result==REG_NOMATCH){
                write_str_to_stream(falseS,buff);
            }else{
                /*Error*/
                break;
            }
        }
    }
    regfree(&regex);
}
