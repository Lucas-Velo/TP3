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

// convrte a data os atributos do struct data
Data parseData(char* s){
    Data d;
    // sscanf lê de uma string na memória, le os numeros da string pulando os traços e armazena os valores nos atributos do struct data
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

// data no modelo pedido xx/xx/xxxx , buffer para a string nao sumir qiando a funcao acaba 
 void formatData(Data d, char* buffer){
    //armazena dados na string ao inves de mostrar na tela
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
    double cilindrada;
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
    // substiuiu o malloc porque o malloc pode dar problema com dados antigos de memoria
    Veiculo *v = calloc(1, sizeof(Veiculo));

    char* parte;
    char combustivel[TAM_STRING];

    //  passa todas as informacoes para o Veiculo
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

Veiculo* lerCsv(char* caminhoArquivo, int *quantidade){
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

    *quantidade = n;

    rewind(arq);
//pula cabeçalho
    fgets(linha, sizeof(linha), arq);
//CRia um vetor de arquivos com alocacao dinamica
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

        // Liberar memoria 
        Veiculo *temp = parseVeiculo(linha);
        retorno[i] = *temp;
        free(temp); 
    }


    fclose(arq);

    return retorno;
}

// compara para depois ser usado na funcao de mediana
int comparar(Veiculo a, Veiculo b){
    // a vem antes de b
    if(a.consumoEstrada < b.consumoEstrada){
        return -1;
    }
    // b vem antes de a
    if(a.consumoEstrada > b.consumoEstrada){
        return 1;
    }

    int comparacaoMarca = strcmp(a.marca, b.marca);

    // a vem antes de b
    if(comparacaoMarca < 0){
        return -1; 
    }
    // b vem ante sde a
    if(comparacaoMarca > 0){
        return 1;
    }

}
//funcao mediana
Veiculo medianaDeTres(Veiculo veiculos[], int esq, int dir){
    int meio = (esq + dir) / 2;

    // copia os tres candidatos
    Veiculo a = veiculos[esq];
    Veiculo b = veiculos[meio];
    Veiculo c = veiculos[dir];

    // coloca a e b em ordem
    if(comparar(a, b) > 0){
        Veiculo temp = a;
        a = b;
        b = temp;
    }

    // coloca a e c em ordem
    if(comparar(a, c) > 0){
        Veiculo temp = a;
        a = c;
        c = temp;
    }

    // coloca b e c em ordem
    if(comparar(b, c) > 0){
        Veiculo temp = b;
        b = c;
        c = temp;
    }

    return b;
}



void quicksort(Veiculo veiculos[], int esq, int dir){
    int i = esq;
    int j= dir;
    Veiculo pivo = medianaDeTres(veiculos, esq, dir);
    
    while(i <= j){
        while(veiculos[i].consumoEstrada < pivo.consumoEstrada ||
             (veiculos[i].consumoEstrada == pivo.consumoEstrada && strcmp(veiculos[i].marca, pivo.marca ) < 0)){
                i++;
             }
        while(veiculos[j].consumoEstrada > pivo.consumoEstrada ||
             (veiculos[j].consumoEstrada == pivo.consumoEstrada && strcmp(veiculos[j].marca, pivo.marca) > 0)){
                j--;
             }
        if(i <=j){
            Veiculo tmp = veiculos[i];
            veiculos[i] = veiculos[j];
            veiculos[j] = tmp;

            i++;
            j--;
        }

    }

    if(esq < j){
        quicksort(veiculos, esq, j);
    }
    if(dir > i){
        quicksort(veiculos, i, dir);
    }
    
}

int main(){
    int quantidadeCsv = 0;

    Veiculo *veiculos = lerCsv("/tmp/veiculos.csv", &quantidadeCsv);

    // Guarda somente os veiculos escolhidos na entrada
    Veiculo *selecionados = malloc(MAX_VEICULOS * sizeof(Veiculo));

    int quantidadeSelecionados = 0;
    int id;

    while(scanf("%d", &id) == 1 && id != -1){

        for(int i = 0; i < quantidadeCsv; i++){

            if(veiculos[i].id == id){

                if(quantidadeSelecionados >= MAX_VEICULOS){
                    free(selecionados);
                    free(veiculos);
                    return 1;
                }

                selecionados[quantidadeSelecionados] = veiculos[i];
                quantidadeSelecionados++;

                break;
            }
        }
    }

    // Ordena apenas os veiculos escolhidos
    if(quantidadeSelecionados > 1){
        quicksort(selecionados, 0, quantidadeSelecionados - 1);
    }

    // Imprime depois da ordenacao
    for(int i = 0; i < quantidadeSelecionados; i++){
        char *str = format(&selecionados[i]);

        printf("%s\n", str);

        free(str);
    }

    free(selecionados);
    free(veiculos);

    return 0;
}