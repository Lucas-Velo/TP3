#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

#define MAX_VEICULOS 1000
#define TAM_STRING 100
#define MAX_COMBUSTIVEL 5

// cria a struct data
typedef struct{
    int dia;
    int mes;
    int ano;
} Data;

// converte a data os atributos do struct data
Data parseData(char* s){
    Data d;
    // sscanf lê de uma string na memória, le os numeros da string pulando os traços e armazena os valores nos atributos do struct data
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

// data no modelo pedido xx/xx/xxxx , buffer para a string nao sumir quando a funcao acaba 
 void formatData(Data d, char* buffer){
    // ALTERAÇÃO MÍNIMA 1: Trocado de %02d para %04d no ano
    sprintf(buffer, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}

// cria a struct veiculo
typedef struct{
    int id;
    char marca[TAM_STRING];
    char modelo[TAM_STRING];
    int ano;
    char categoria[TAM_STRING];
    char combustivel[MAX_COMBUSTIVEL][TAM_STRING];
    int cilindros;
    float cilindrada;
    char transmissao[TAM_STRING];
    char tracao[TAM_STRING];
    double consumoCidade;
    double consumoEstrada;
    double co2;
    bool turbo;
    Data dataRegistro;
} Veiculo;

// funcao para criar um Veiculo com as informacoes do arquivo
Veiculo* parseVeiculo(char* s){
    Veiculo *v = malloc(sizeof(Veiculo));

    char* parte;
    char combustivel[TAM_STRING];

    // passa todas as informacoes para o Veiculo
    parte = strtok(s, ",");
    //converte a forma que recebe em csv e passa pra int
    v->id = atoi(parte);

    parte = strtok(NULL, ",");
    sprintf(v->marca, "%s", parte);

    parte = strtok(NULL, ",");
    sprintf(v->modelo, "%s", parte);

    parte = strtok(NULL, ",");
    v->ano = atoi(parte);

    parte = strtok(NULL, ",");
    sprintf(v->categoria, "%s", parte);

    parte = strtok(NULL, ",");
    sprintf(combustivel, "%s", parte);

    parte = strtok(NULL, ",");
    v->cilindros = atoi(parte);

    parte = strtok(NULL, ",");
    v->cilindrada = atof(parte);

    parte = strtok(NULL, ",");
    strcpy(v->transmissao, parte);

    parte = strtok(NULL, ",");
    strcpy(v->tracao, parte);

    parte = strtok(NULL, ",");
    v->consumoCidade = atof(parte);

    parte = strtok(NULL, ",");
    v->consumoEstrada = atof(parte);

    parte = strtok(NULL, ",");
    v->co2 = atof(parte);

    parte = strtok(NULL, ",");
    v->turbo = (strcmp(parte, "true") == 0);

    parte = strtok(NULL, ",");
    v->dataRegistro = parseData(parte);

    //separa os combustíveis
    int quantComb = 0;
    char *comb = strtok(combustivel, ";");

    while(comb != NULL && quantComb < MAX_COMBUSTIVEL){
        sprintf(v->combustivel[quantComb], "%s", comb);
        quantComb++;
        //identifica a parte separada por ; em combustivel para trocar
        comb = strtok(NULL, ";");
    }

    return v;
}

char* format(Veiculo *v){
    // cria uma string para formatar
    char* str = malloc(2000 * sizeof(char));
    int pos=0;

    //adiciona a string quantos caracteres que ela acabou de escrever acelerando o processo
    pos+=sprintf(str+pos, "[%d ## ", v->id);
    pos+=sprintf(str+pos, "%s ## ", v->marca);
    pos+=sprintf(str+pos, "%s ## ", v->modelo);
    pos+=sprintf(str+pos, "%d ## ", v->ano);
    pos+=sprintf(str+pos, "%s ## ", v->categoria);

    pos+=sprintf(str+pos, "[");

    //Separa os combustiveis com , se tiver mais de um
    for(int i=0; i<MAX_COMBUSTIVEL && v->combustivel[i][0] !='\0'; i++){
        if(i > 0){
            pos += sprintf(str + pos, ",");
        }
        pos += sprintf(str + pos, "%s", v->combustivel[i]);
    }

    pos += sprintf(str + pos, "] ## ");

    pos += sprintf(str + pos, "%d ## ", v->cilindros);
    pos += sprintf(str + pos, "%.1f ## ", v->cilindrada);
    pos += sprintf(str + pos, "%s ## ", v->transmissao);
    pos += sprintf(str + pos, "%s ## ", v->tracao);
    pos += sprintf(str + pos, "%.2f ## ", v->consumoCidade);
    pos += sprintf(str + pos, "%.2f ## ", v->consumoEstrada);
    pos += sprintf(str + pos, "%.1f ## ", v->co2);
    pos += sprintf(str + pos, "%s ## ", v->turbo ? "true" : "false");

    //chama a funcao para formatar a data
    char buffer[20];
    formatData(v->dataRegistro, buffer);

    pos += sprintf(str + pos, "%s]", buffer);

    return str;
}

Veiculo* lerCsv(char* caminhoArquivo){
    //abre o arquivo para leitura
    FILE *arq = fopen(caminhoArquivo, "r");

    //se der erro ao procurar o arquivo
    if(arq==NULL){
        printf("Erro ao ler arquivo\n");
        exit(1);
    }

    char linha[2000];
    int n=0;

    // pula cabeçalho
    fgets(linha, sizeof(linha), arq);

    //Conta a quantidade de linhas do arquivo
    while((fgets(linha, sizeof(linha), arq))!=NULL){
        n++; 
    }

    rewind(arq);
    //pula cabeçalho
    fgets(linha, sizeof(linha), arq);
    
    //Cria um vetor de arquivos com alocacao dinamica
    Veiculo *retorno = malloc(n * sizeof(Veiculo)); 
    
    //percorre as linhas preenchidas do arquivo
    for(int i = 0; i < n; i++){
        fgets(linha, sizeof(linha), arq);

        //tirar o /n
        for(int j = 0; linha[j] != '\0'; j++){
            if(linha[j] == '\n'){
                linha[j] = '\0';
                break;
            }
        }

        // Adiciona o veiculo ja pronto ao vetor
        retorno[i] = *parseVeiculo(linha);
    }

    fclose(arq);
    return retorno;
}

// Funcao manual para converter string para minusculo (soma 32 na tabela ASCII)
void toLowerCase(char* origem, char* destino) {
    int i = 0;
    while (origem[i] != '\0') {
        if (origem[i] >= 'A' && origem[i] <= 'Z') {
            destino[i] = origem[i] + 32;
        } else {
            destino[i] = origem[i];
        }
        i++;
    }
    destino[i] = '\0';
}

// Algoritmo de Ordenação por Seleção
void ordenacaoSelecao(Veiculo *vetor, int tamanho){
    char modeloJ[TAM_STRING];
    char modeloMenor[TAM_STRING];

    for(int i = 0; i < tamanho - 1; i++){
        int indiceMenor = i;
        
        for(int j = i + 1; j < tamanho; j++){
            
            // Converte os modelos para minusculo em variaveis auxiliares antes de comparar
            toLowerCase(vetor[j].modelo, modeloJ);
            toLowerCase(vetor[indiceMenor].modelo, modeloMenor);
            
            // Verificar desempate de forma insensivel a maiusculas/minusculas
            int comparacao = strcmp(modeloJ, modeloMenor);
            
            // modelo for menor ou se o modelo for = e o id for menor)
            if(comparacao < 0 || (comparacao == 0 && vetor[j].id < vetor[indiceMenor].id)){
                indiceMenor = j; 
            }
        }
        
        // faz a troca de posicao (swap) copiando a struct inteira
        if(indiceMenor != i){
            Veiculo temp = vetor[i];
            vetor[i] = vetor[indiceMenor];
            vetor[indiceMenor] = temp;
        }
    }
}

int main(){
    //Procura o arquivo na pasta tmp
    Veiculo* veiculos = lerCsv("/tmp/veiculos.csv");

    Veiculo selecionados[MAX_VEICULOS];
    int qtdSelecionados = 0;

    int id;
    scanf("%d", &id);

    //Continua enquanto a entrada nao for -1
    while(id != -1){
        
        //procura pelo id do veiculo no arquivo
        for(int i = 0; i < MAX_VEICULOS; i++){
            if(veiculos[i].id == id){
                
                selecionados[qtdSelecionados] = veiculos[i];
                qtdSelecionados++;
                break;
            }
        }
        scanf("%d", &id);
    }

    //desempate e ignorando letras maiusculas
    ordenacaoSelecao(selecionados, qtdSelecionados);

    // Imprime na tela o vetor "selecionados" ordenado
    for(int i = 0; i < qtdSelecionados; i++){
        char *str = format(&selecionados[i]);
        printf("%s\n", str);
        free(str);
    }

    free(veiculos);
    return 0;
}