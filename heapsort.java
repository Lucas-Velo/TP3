import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class heapsort {

    public static class Data {
        private String ano;
        private String mes;
        private String dia;

        // separa os campos da data
        public Data(String s) {
            String[] partes = s.split("-");

            ano = partes[0];
            mes = partes[1];
            dia = partes[2];
        }

        //formata a data como dd/mm/aaaa
        public String format() {
            return dia + "/" + mes + "/" + ano;
        }
    }

    public static class Veiculo {
        private int id;
        private String marca;
        private String modelo;
        private int ano;
        private String categoria;
        private String[] combustivel;
        private int cilindros;
        private double cilindrada;
        private String transmissao;
        private String tracao;
        private double consumoCidade;
        private double consumoEstrada;
        private double co2;
        private boolean turbo;
        private Data dataRegistro;

        // transforma a linha do csv em um veiculo
        public Veiculo(String s) {
            String[] partes = s.split(",");

            id = Integer.parseInt(partes[0]);
            marca = partes[1];
            modelo = partes[2];
            ano = Integer.parseInt(partes[3]);
            categoria = partes[4];
            combustivel = partes[5].split(";");
            cilindros = Integer.parseInt(partes[6]);
            cilindrada = Double.parseDouble(partes[7]);
            transmissao = partes[8];
            tracao = partes[9];
            consumoCidade = Double.parseDouble(partes[10]);
            consumoEstrada = Double.parseDouble(partes[11]);
            co2 = Double.parseDouble(partes[12]);
            turbo = partes[13].equals("true");
            dataRegistro = new Data(partes[14]);
        }

        public int getId() {
            return id;
        }

        public double getCo2() {
            return co2;
        }

        public String getTransmissao() {
            return transmissao;
        }

        //formata o registro sem ajustar as casas decimais
        public String format() {
            String combustiveis = "[";

            for (int i = 0; i < combustivel.length; i++) {
                if (i > 0) {
                    combustiveis += ", ";
                }

                combustiveis += combustivel[i];
            }

            combustiveis += "]";

            return "[" + id
                + " ## " + marca
                + " ## " + modelo
                + " ## " + ano
                + " ## " + categoria
                + " ## " + combustiveis
                + " ## " + cilindros
                + " ## " + cilindrada
                + " ## " + transmissao
                + " ## " + tracao
                + " ## " + consumoCidade
                + " ## " + consumoEstrada
                + " ## " + co2
                + " ## " + turbo
                + " ## " + dataRegistro.format()
                + "]";
        }
    }

    // le os veiculos do arquivo csv
    public static Veiculo[] lerCsv(String caminhoArquivo)
            throws IOException {

        BufferedReader arq = new BufferedReader(new FileReader(caminhoArquivo));

        int quantidade = 0;

        //pula o cabecalho
        arq.readLine();

        while (arq.readLine() != null) {
            quantidade++;
        }

        arq.close();

        Veiculo[] veiculos = new Veiculo[quantidade];

        // abre novamente para preencher o vetor
        arq = new BufferedReader(new FileReader(caminhoArquivo));

        arq.readLine();

        for (int i = 0; i < quantidade; i++) {
            veiculos[i] = new Veiculo(arq.readLine());
        }

        arq.close();

        return veiculos;
    }

    //ordena por co2 e desempata pela transmissao
    public static void heapsort(Veiculo[] vetor, int tamanho) {
        if (tamanho < 2) {
            return;
        }

        // monta o heap e depois retira os maiores elementos
        for (int etapa = tamanho / 2 - 1;
             etapa >= -(tamanho - 1);
             etapa--) {

            int pai;
            int tamanhoHeap;

            if (etapa >= 0) {
                //constroi o heap de baixo para cima
                pai = etapa;
                tamanhoHeap = tamanho;
            } else {
                // coloca o maior na ultima posicao do heap
                int fim = tamanho + etapa;

                Veiculo temp = vetor[0];
                vetor[0] = vetor[fim];
                vetor[fim] = temp;

                pai = 0;
                tamanhoHeap = fim;
            }

            //faz o elemento descer para ajustar o heap
            int filhoEsquerdo = 2 * pai + 1;

            while (filhoEsquerdo < tamanhoHeap) {
                int maiorFilho = filhoEsquerdo;
                int filhoDireito = filhoEsquerdo + 1;

                // escolhe o maior filho
                if (filhoDireito < tamanhoHeap) {
                    if (vetor[filhoDireito].getCo2() > vetor[filhoEsquerdo].getCo2() ||

                        (vetor[filhoDireito].getCo2() == vetor[filhoEsquerdo].getCo2() &&

                         vetor[filhoDireito].getTransmissao().compareTo(vetor[filhoEsquerdo].getTransmissao()) > 0)) {

                        maiorFilho = filhoDireito;
                    }
                }

                //troca se o maior filho for maior que o pai
                if (vetor[maiorFilho].getCo2() > vetor[pai].getCo2() ||

                    (vetor[maiorFilho].getCo2() == vetor[pai].getCo2() &&

                     vetor[maiorFilho].getTransmissao().compareTo(vetor[pai].getTransmissao()) > 0)) {

                    Veiculo temp = vetor[pai];
                    vetor[pai] = vetor[maiorFilho];
                    vetor[maiorFilho] = temp;

                    pai = maiorFilho;
                    filhoEsquerdo = 2 * pai + 1;
                } else {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Veiculo[] veiculos = lerCsv("/tmp/veiculos.csv");

        // guarda os veiculos escolhidos na entrada
        Veiculo[] selecionados = new Veiculo[1000];
        int qtdSelecionados = 0;

        Scanner entrada = new Scanner(System.in);

        while (entrada.hasNextInt()) {
            int id = entrada.nextInt();

            if (id == -1) {
                break;
            }

            //procura o veiculo pelo id
            for (int i = 0; i < veiculos.length; i++) {
                if (veiculos[i].getId() == id) {

                    if (qtdSelecionados >= selecionados.length) {
                        entrada.close();
                        return;
                    }

                    selecionados[qtdSelecionados] = veiculos[i];
                    qtdSelecionados++;

                    break;
                }
            }
        }

        heapsort(selecionados, qtdSelecionados);

        // imprime os veiculos ordenados
        for (int i = 0; i < qtdSelecionados; i++) {
            System.out.println(selecionados[i].format());
        }

        entrada.close();
    }
}