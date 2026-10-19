import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class selecaoMain {

    public static class Data {
        private int dia;
        private int mes;
        private int ano;

        // separa os campos da data
        public Data(String s) {
            String[] partes = s.split("-");

            ano = Integer.parseInt(partes[0]);
            mes = Integer.parseInt(partes[1]);
            dia = Integer.parseInt(partes[2]);
        }

        public int getDia() {
            return dia;
        }

        public int getMes() {
            return mes;
        }

        public int getAno() {
            return ano;
        }

        //formata a data como dd/mm/aaaa
        public String format() {
            return String.format(
                "%02d/%02d/%04d", dia, mes, ano
            );
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
        private float cilindrada;
        private String transmissao;
        private String tracao;
        private double consumoCidade;
        private double consumoEstrada;
        private double co2;
        private boolean turbo;
        private Data dataRegistro;

        // transforma a linha do csv em um veiculo
        public Veiculo parseVeiculo(String s) {
            String[] partes = s.split(",");

            //retira os espacos das extremidades dos campos
            for (int i = 0; i < partes.length; i++) {
                partes[i] = retirarEspacos(partes[i]);
            }

            id = Integer.parseInt(partes[0]);
            marca = partes[1];
            modelo = partes[2];
            ano = Integer.parseInt(partes[3]);
            categoria = partes[4];

            // separa os combustiveis pelo ponto e virgula
            combustivel = partes[5].split(";");

            cilindros = Integer.parseInt(partes[6]);
            cilindrada = Float.parseFloat(partes[7]);
            transmissao = partes[8];
            tracao = partes[9];
            consumoCidade = Double.parseDouble(partes[10]);
            consumoEstrada = Double.parseDouble(partes[11]);
            co2 = Double.parseDouble(partes[12]);
            turbo = partes[13].equals("true");
            dataRegistro = new Data(partes[14]);

            return this;
        }

        public int getId() {
            return id;
        }

        public String getMarca() {
            return marca;
        }

        public String getModelo() {
            return modelo;
        }

        public int getAno() {
            return ano;
        }

        public String getCategoria() {
            return categoria;
        }

        public String[] getCombustivel() {
            return combustivel;
        }

        public int getCilindros() {
            return cilindros;
        }

        public float getCilindrada() {
            return cilindrada;
        }

        public String getTransmissao() {
            return transmissao;
        }

        public String getTracao() {
            return tracao;
        }

        public double getConsumoCidade() {
            return consumoCidade;
        }

        public double getConsumoEstrada() {
            return consumoEstrada;
        }

        public double getCo2() {
            return co2;
        }

        public boolean getTurbo() {
            return turbo;
        }

        public Data getDataRegistro() {
            return dataRegistro;
        }

        //formata os dados do veiculo
        public String format() {
            String combustiveis = "[";

            for (int i = 0; i < combustivel.length; i++) {
                if (i > 0) {
                    combustiveis += ",";
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
                + " ## " + formatarDecimal(cilindrada, 1)
                + " ## " + transmissao
                + " ## " + tracao
                + " ## " + formatarDecimal(consumoCidade, 2)
                + " ## " + formatarDecimal(consumoEstrada, 2)
                + " ## " + formatarDecimal(co2, 1)
                + " ## " + turbo
                + " ## " + dataRegistro.format()
                + "]";
        }
    }

    // retira espacos das extremidades sem usar trim
    public static String retirarEspacos(String s) {
        int inicio = 0;
        int fim = s.length() - 1;

        while (inicio <= fim && s.charAt(inicio) <= ' ') {
            inicio++;
        }

        while (fim >= inicio && s.charAt(fim) <= ' ') {
            fim--;
        }

        String resultado = "";

        for (int i = inicio; i <= fim; i++) {
            resultado += s.charAt(i);
        }

        return resultado;
    }

    //formata os numeros com uma ou duas casas decimais
    public static String formatarDecimal(double valor, int casas) {
        String texto;

        if (casas == 1) {
            texto = String.format("%.1f", valor);
        } else {
            texto = String.format("%.2f", valor);
        }

        String resultado = "";

        // garante ponto decimal sem usar locale ou replace
        for (int i = 0; i < texto.length(); i++) {
            char caractere = texto.charAt(i);

            if (caractere == ',') {
                resultado += '.';
            } else {
                resultado += caractere;
            }
        }

        return resultado;
    }

    // le os veiculos do arquivo csv
    public static Veiculo[] lerCsv(String caminhoArquivo)
            throws IOException {

        int n = 0;

        //conta os registros
        try (BufferedReader arq =
                new BufferedReader(new FileReader(caminhoArquivo))) {

            // pula o cabecalho
            arq.readLine();

            while (arq.readLine() != null) {
                n++;
            }
        }

        Veiculo[] retorno = new Veiculo[n];

        //abre novamente para preencher o vetor
        try (BufferedReader arq =
                new BufferedReader(new FileReader(caminhoArquivo))) {

            arq.readLine();

            for (int i = 0; i < n; i++) {
                String linha = arq.readLine();

                retorno[i] = new Veiculo();
                retorno[i].parseVeiculo(linha);
            }
        }

        return retorno;
    }

    // converte letras maiusculas para minusculas manualmente
    public static String toLowerCase(String origem) {
        String destino = "";

        for (int i = 0; i < origem.length(); i++) {
            char letra = origem.charAt(i);

            if (letra >= 'A' && letra <= 'Z') {
                letra = (char) (letra + 32);
            }

            destino += letra;
        }

        return destino;
    }

    //ordenacao por selecao usando modelo e desempate por id
    public static void ordenacaoSelecao(
            Veiculo[] vetor, int tamanho) {

        for (int i = 0; i < tamanho - 1; i++) {
            int indiceMenor = i;

            for (int j = i + 1; j < tamanho; j++) {
                String modeloJ =
                    toLowerCase(vetor[j].getModelo());

                String modeloMenor =
                    toLowerCase(vetor[indiceMenor].getModelo());

                int comparacao = modeloJ.compareTo(modeloMenor);

                if (comparacao < 0 ||
                    (comparacao == 0 &&
                     vetor[j].getId() <
                     vetor[indiceMenor].getId())) {

                    indiceMenor = j;
                }
            }

            // troca as referencias dos veiculos
            if (indiceMenor != i) {
                Veiculo temp = vetor[i];
                vetor[i] = vetor[indiceMenor];
                vetor[indiceMenor] = temp;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        Veiculo[] veiculos = lerCsv("/tmp/veiculos.csv");

        //guarda os veiculos escolhidos na entrada
        Veiculo[] selecionados = new Veiculo[1000];
        int qtdSelecionados = 0;

        Scanner entrada = new Scanner(System.in);

        while (entrada.hasNextInt()) {
            int id = entrada.nextInt();

            if (id == -1) {
                break;
            }

            // procura o veiculo pelo id
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

        ordenacaoSelecao(selecionados, qtdSelecionados);

        //imprime depois da ordenacao
        for (int i = 0; i < qtdSelecionados; i++) {
            System.out.println(selecionados[i].format());
        }

        entrada.close();
    }
}

