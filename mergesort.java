import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class mergesort {

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

        public double getConsumoCidade() {
            return consumoCidade;
        }

        public String getCategoria() {
            return categoria;
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

    //divide o trecho e ordena as duas metades
    public static void mergesort(
            Veiculo[] vetor, int esq, int dir) {

        if (esq < dir) {
            int meio = (esq + dir) / 2;

            mergesort(vetor, esq, meio);
            mergesort(vetor, meio + 1, dir);

            intercalar(vetor, esq, meio, dir);
        }
    }

    // junta as duas metades ordenadas
    public static void intercalar(
            Veiculo[] vetor, int esq, int meio, int dir) {

        int nEsq = meio - esq + 1;
        int nDir = dir - meio;

        Veiculo[] vetEsq = new Veiculo[nEsq];
        Veiculo[] vetDir = new Veiculo[nDir];

        //copia a metade esquerda
        for (int i = 0; i < nEsq; i++) {
            vetEsq[i] = vetor[esq + i];
        }

        // copia a metade direita
        for (int i = 0; i < nDir; i++) {
            vetDir[i] = vetor[meio + 1 + i];
        }

        int iEsq = 0;
        int iDir = 0;
        int k = esq;

        //compara por consumo na cidade e desempata por categoria
        while (iEsq < nEsq && iDir < nDir) {

            if (vetEsq[iEsq].getConsumoCidade() < vetDir[iDir].getConsumoCidade() ||

                (vetEsq[iEsq].getConsumoCidade() == vetDir[iDir].getConsumoCidade() &&

                 vetEsq[iEsq].getCategoria().compareTo(vetDir[iDir].getCategoria()) <= 0)) {

                vetor[k] = vetEsq[iEsq];
                iEsq++;
            } else {
                vetor[k] = vetDir[iDir];
                iDir++;
            }

            k++;
        }

        // copia os elementos que sobraram na esquerda
        while (iEsq < nEsq) {
            vetor[k] = vetEsq[iEsq];
            iEsq++;
            k++;
        }

        //copia os elementos que sobraram na direita
        while (iDir < nDir) {
            vetor[k] = vetDir[iDir];
            iDir++;
            k++;
        }
    }

    public static void main(String[] args) throws IOException {
        Veiculo[] veiculos = lerCsv("/tmp/veiculos.csv");

        Veiculo[] selecionados = new Veiculo[1000];
        int qtdSelecionados = 0;

        Scanner entrada = new Scanner(System.in);

        // le os ids ate encontrar -1
        while (entrada.hasNextInt()) {
            int id = entrada.nextInt();

            if (id == -1) {
                break;
            }

            //procura o veiculo correspondente ao id
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

        mergesort(selecionados, 0, qtdSelecionados - 1);

        // imprime os veiculos ordenados
        for (int i = 0; i < qtdSelecionados; i++) {
            System.out.println(selecionados[i].format());
        }

        entrada.close();
    }
}