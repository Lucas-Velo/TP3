import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class quicksortListaDupla {

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

        public String getMarca() {
            return marca;
        }

        public double getConsumoEstrada() {
            return consumoEstrada;
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

    public static class Celula {
        private Veiculo elemento;
        private Celula ant;
        private Celula prox;

        // cria uma celula da lista dupla
        public Celula(Veiculo elemento) {
            this.elemento = elemento;
            ant = null;
            prox = null;
        }
    }

    public static class ListaDupla {
        private Celula primeiro;
        private Celula ultimo;
        private int tamanho;

        //cria a cabeca da lista
        public ListaDupla() {
            primeiro = new Celula(null);
            ultimo = primeiro;
            tamanho = 0;
        }

        // insere um veiculo no final
        public void inserirFim(Veiculo elemento) {
            Celula nova = new Celula(elemento);

            ultimo.prox = nova;
            nova.ant = ultimo;

            ultimo = nova;
            tamanho++;
        }

        //inicia o quicksort
        public void quicksort() {
            if (tamanho > 1) {
                quicksort(
                    primeiro.prox, ultimo, 0, tamanho - 1
                );
            }
        }

        // ordena diretamente pelas celulas da lista dupla
        private void quicksort(
                Celula esquerda, Celula direita,
                int esq, int dir) {

            int i = esq;
            int j = dir;

            Celula celulaI = esquerda;
            Celula celulaJ = direita;

            //localiza a celula do meio do trecho
            int meio = (esq + dir) / 2;
            Celula celulaPivo = esquerda;

            for (int pos = esq; pos < meio; pos++) {
                celulaPivo = celulaPivo.prox;
            }

            Veiculo pivo = celulaPivo.elemento;

            while (i <= j) {

                // avanca enquanto o veiculo vem antes do pivo
                while (
                    celulaI.elemento.getConsumoEstrada()
                        < pivo.getConsumoEstrada() ||

                    (celulaI.elemento.getConsumoEstrada()
                        == pivo.getConsumoEstrada() &&

                     celulaI.elemento.getMarca().compareTo(
                         pivo.getMarca()) < 0)
                ) {
                    celulaI = celulaI.prox;
                    i++;
                }

                //recua enquanto o veiculo vem depois do pivo
                while (
                    celulaJ.elemento.getConsumoEstrada()
                        > pivo.getConsumoEstrada() ||

                    (celulaJ.elemento.getConsumoEstrada()
                        == pivo.getConsumoEstrada() &&

                     celulaJ.elemento.getMarca().compareTo(
                         pivo.getMarca()) > 0)
                ) {
                    celulaJ = celulaJ.ant;
                    j--;
                }

                if (i <= j) {
                    // troca os veiculos entre as celulas
                    Veiculo temp = celulaI.elemento;
                    celulaI.elemento = celulaJ.elemento;
                    celulaJ.elemento = temp;

                    celulaI = celulaI.prox;
                    celulaJ = celulaJ.ant;

                    i++;
                    j--;
                }
            }

            //ordena o trecho da esquerda
            if (esq < j) {
                quicksort(esquerda, celulaJ, esq, j);
            }

            // ordena o trecho da direita
            if (i < dir) {
                quicksort(celulaI, direita, i, dir);
            }
        }

        //imprime os veiculos sem mostrar a cabeca
        public void mostrar() {
            for (Celula i = primeiro.prox;
                 i != null;
                 i = i.prox) {

                System.out.println(i.elemento.format());
            }
        }
    }

    // le os veiculos do arquivo csv
    public static Veiculo[] lerCsv(String caminhoArquivo)
            throws IOException {

        BufferedReader arq =
            new BufferedReader(new FileReader(caminhoArquivo));

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

    public static void main(String[] args) throws IOException {
        Veiculo[] veiculos = lerCsv("/tmp/veiculos.csv");

        //guarda os veiculos escolhidos na lista dupla
        ListaDupla selecionados = new ListaDupla();

        Scanner entrada = new Scanner(System.in);

        while (entrada.hasNextInt()) {
            int id = entrada.nextInt();

            if (id == -1) {
                break;
            }

            // procura o veiculo pelo id
            for (int i = 0; i < veiculos.length; i++) {
                if (veiculos[i].getId() == id) {
                    selecionados.inserirFim(veiculos[i]);
                    break;
                }
            }
        }

        selecionados.quicksort();
        selecionados.mostrar();

        entrada.close();
    }
}