import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/*
 * =====================================================================
 *  COMERCIO.JAVA  –  O SISTEMA COMPLETO (programa principal com menu)
 * =====================================================================
 *  Junta tudo das oficinas de PILHAS e FILAS:
 *   - finalizarPedido: guarda o pedido na PILHA (recentes) e na FILA (chegada)
 *   - Listar produtos dos pedidos mais recentes          -> PILHA.subPilha
 *   - Valor total médio dos N primeiros pedidos          -> FILA.calcularValorMedio
 *   - Primeiros pedidos com valor total acima de X       -> FILA.filtrar
 *   - Primeiros pedidos que apresentam um produto        -> FILA.filtrar
 *  + extras que podem cair (mais recente, atender pedido, contar, somar).
 *
 *  PRECISA DE: EstruturasLineares.java e Produtos.java na mesma pasta.
 *  RODAR:  javac -d out *.java    depois    java -cp out Comercio
 *  Dica: use a opção 9 pra gerar pedidos de exemplo e testar rápido.
 * =====================================================================
 */
public class Comercio {

    static final String ARQUIVO_PRODUTOS = "produtos.txt";

    static List<Produto> produtosCadastrados = new ArrayList<>();
    static Pilha<Pedido> pilhaPedidos = new Pilha<>();   // mais recente no topo
    static Fila<Pedido> filaPedidos = new Fila<>();      // ordem de chegada
    static Scanner teclado = new Scanner(System.in);

    public static void main(String[] args) {
        carregarProdutos(ARQUIVO_PRODUTOS);

        int opcao;
        do {
            opcao = menu();
            try {
                switch (opcao) {
                    case 1:  listarProdutos(); break;
                    case 2:  procurarProduto(); break;
                    case 3:  iniciarPedido(); break;
                    case 4:  listarTodosPedidos(); break;
                    case 5:  listarProdutosPedidosRecentes(); break;
                    case 6:  exibirValorMedioPrimeirosPedidos(); break;
                    case 7:  exibirPedidosAcimaDeValor(); break;
                    case 8:  exibirPedidosComProduto(); break;
                    case 9:  gerarPedidosExemplo(); break;
                    case 10: exibirPedidoMaisRecente(); break;
                    case 11: atenderProximoPedido(); break;
                    case 0:  System.out.println("Saindo..."); break;
                    default: System.out.println("Opcao invalida!");
                }
            } catch (IllegalStateException | IllegalArgumentException e) {
                System.out.println("ATENCAO: " + e.getMessage());   // pilha/fila vazia, valor inválido...
            }
        } while (opcao != 0);
    }

    static int menu() {
        System.out.println("\n========== COMERCIO ==========");
        System.out.println(" 1 - Listar produtos");
        System.out.println(" 2 - Procurar produto");
        System.out.println(" 3 - Iniciar novo pedido");
        System.out.println(" 4 - Listar todos os pedidos (ordem de chegada)");
        System.out.println(" 5 - Listar produtos dos pedidos mais recentes");
        System.out.println(" 6 - Exibir o valor total medio dos N primeiros pedidos");
        System.out.println(" 7 - Exibir os primeiros pedidos com valor total acima de um valor");
        System.out.println(" 8 - Exibir os primeiros pedidos que apresentam um produto");
        System.out.println(" 9 - Gerar pedidos de exemplo (teste)");
        System.out.println("10 - Exibir o pedido mais recente");
        System.out.println("11 - Atender (remover) o proximo pedido da fila");
        System.out.println(" 0 - Sair");
        return lerInt("Opcao: ");
    }

    // ===================== PRODUTOS =====================

    /** Lê o arquivo de produtos (cria um de exemplo se não existir). */
    static void carregarProdutos(String nomeArquivo) {
        Path arq = Paths.get(nomeArquivo);
        try {
            if (!Files.exists(arq)) criarArquivoExemplo(arq);
            for (String linha : Files.readAllLines(arq, StandardCharsets.UTF_8)) {
                if (linha.trim().isEmpty()) continue;
                try {
                    produtosCadastrados.add(Produto.criarDoTexto(linha));
                } catch (RuntimeException e) {
                    System.out.println("Linha ignorada (" + e.getMessage() + "): " + linha);
                }
            }
            System.out.println(produtosCadastrados.size() + " produtos carregados de " + nomeArquivo);
        } catch (IOException e) {
            System.out.println("Erro ao ler arquivo: " + e.getMessage());
        }
    }

    static void listarProdutos() {
        System.out.println("\n--- Produtos cadastrados ---");
        for (int i = 0; i < produtosCadastrados.size(); i++)
            System.out.printf("%2d - %s%n", i + 1, produtosCadastrados.get(i));
    }

    static void procurarProduto() {
        Produto p = buscarProdutoPorDescricao(lerTexto("Descricao: "));
        System.out.println(p == null ? "Produto nao encontrado." : p);
    }

    static Produto buscarProdutoPorDescricao(String descricao) {
        for (Produto p : produtosCadastrados)
            if (p.getDescricao().equalsIgnoreCase(descricao.trim())) return p;
        return null;
    }

    // ===================== PEDIDOS =====================

    /** Monta um pedido escolhendo produtos pelo número. */
    static void iniciarPedido() {
        Pedido pedido = new Pedido();
        listarProdutos();
        int num;
        do {
            num = lerInt("Numero do produto (0 para finalizar): ");
            if (num >= 1 && num <= produtosCadastrados.size()) {
                if (pedido.incluirProduto(produtosCadastrados.get(num - 1)))
                    System.out.println("Incluido! Total parcial: R$ " + String.format("%.2f", pedido.valorFinal()));
                else
                    System.out.println("Pedido cheio!");
            } else if (num != 0) {
                System.out.println("Produto invalido.");
            }
        } while (num != 0);

        if (pedido.getQuantProdutos() > 0) finalizarPedido(pedido);
        else System.out.println("Pedido vazio, descartado.");
    }

    /**
     * >>> OFICINA PILHAS – TAREFA 1  +  OFICINA FILAS – TAREFA 0 <<<
     * FINALIZAR PEDIDO
     * Guarda nas duas estruturas:
     *   PILHA -> pra achar os mais RECENTES
     *   FILA  -> pra manter a ordem de CHEGADA
     * Se a prova pedir só uma, apague a outra linha.
     */
    static void finalizarPedido(Pedido pedido) {
        pilhaPedidos.empilhar(pedido);
        filaPedidos.enfileirar(pedido);
        System.out.println("Pedido finalizado:\n" + pedido);
    }

    static void listarTodosPedidos() {
        if (filaPedidos.vazia()) { System.out.println("Nenhum pedido."); return; }
        System.out.println("\n--- Pedidos (ordem de chegada) ---");
        filaPedidos.paraCada(p -> System.out.println(p));
    }

    /**
     * >>> OFICINA PILHAS – TAREFA 3 <<<
     * LISTAR PRODUTOS DOS PEDIDOS MAIS RECENTES
     * subPilha(n) pega os n do topo = n mais recentes.
     */
    static void listarProdutosPedidosRecentes() {
        int n = lerInt("Quantos pedidos mais recentes? ");
        Pilha<Pedido> recentes = pilhaPedidos.subPilha(n);
        if (recentes.vazia()) { System.out.println("Nenhum pedido."); return; }
        System.out.println("\n--- Produtos dos " + recentes.tamanho() + " pedidos mais recentes ---");
        while (!recentes.vazia()) {                       // desempilha a CÓPIA, não a original
            Pedido p = recentes.desempilhar();
            System.out.println("Pedido " + p.getIdPedido() + ":");
            for (Produto prod : p.getProdutos()) System.out.println("   " + prod);
        }
    }

    /** >>> OFICINA FILAS – TAREFA 3 (opção a) <<<  VALOR TOTAL MÉDIO DOS N PRIMEIROS PEDIDOS */
    static void exibirValorMedioPrimeirosPedidos() {
        int n = lerInt("Quantos primeiros pedidos? ");
        double media = filaPedidos.calcularValorMedio(Pedido::valorFinal, n);
        System.out.printf("Valor total medio dos %d primeiros pedidos: R$ %.2f%n",
                Math.min(n, filaPedidos.tamanho()), media);
    }

    /** >>> OFICINA FILAS – TAREFA 3 (opção b) <<<  PRIMEIROS PEDIDOS COM VALOR TOTAL ACIMA DE X */
    static void exibirPedidosAcimaDeValor() {
        int n = lerInt("Quantos primeiros pedidos analisar? ");
        double valor = lerDouble("Valor minimo (R$): ");
        Fila<Pedido> resultado = filaPedidos.filtrar(p -> p.valorFinal() > valor, n);
        mostrarResultado(resultado, "com valor total acima de R$ " + String.format("%.2f", valor));
    }

    /** >>> OFICINA FILAS – TAREFA 3 (opção c) <<<  PRIMEIROS PEDIDOS QUE APRESENTAM UM PRODUTO */
    static void exibirPedidosComProduto() {
        int n = lerInt("Quantos primeiros pedidos analisar? ");
        Produto produto = buscarProdutoPorDescricao(lerTexto("Descricao do produto: "));
        if (produto == null) { System.out.println("Produto nao encontrado."); return; }
        Fila<Pedido> resultado = filaPedidos.filtrar(p -> p.contemProduto(produto), n);
        mostrarResultado(resultado, "com o produto " + produto.getDescricao());
    }

    // ----- EXTRAS que podem cair -----

    /** Pedido mais recente = topo da pilha (sem remover). */
    static void exibirPedidoMaisRecente() {
        System.out.println("Mais recente:\n" + pilhaPedidos.consultarTopo());
    }

    /** "Atender" = tirar o primeiro da fila (o mais antigo). */
    static void atenderProximoPedido() {
        Pedido p = filaPedidos.desenfileirar();
        System.out.println("Atendido:\n" + p);
        System.out.println("Restam " + filaPedidos.tamanho() + " pedido(s) na fila.");
        // Obs.: ele continua na pilha. Se a prova pedir pra tirar das duas, explique/ajuste.
    }

    static void mostrarResultado(Fila<Pedido> resultado, String descricao) {
        if (resultado.vazia()) { System.out.println("Nenhum pedido " + descricao + "."); return; }
        System.out.println("\n--- " + resultado.tamanho() + " pedido(s) " + descricao + " ---");
        resultado.paraCada(p -> System.out.println(p));
        System.out.printf("Soma: R$ %.2f%n", resultado.somar(Pedido::valorFinal, resultado.tamanho()));
    }

    // ===================== TESTE RÁPIDO =====================

    /** Cria 6 pedidos com os produtos carregados (pra não digitar tudo). */
    static void gerarPedidosExemplo() {
        if (produtosCadastrados.isEmpty()) { System.out.println("Sem produtos."); return; }
        int[][] combinacoes = { {0, 1}, {2}, {0, 3, 4}, {5, 1}, {6, 7, 0}, {3} };
        for (int[] comb : combinacoes) {
            Pedido p = new Pedido();
            for (int idx : comb) p.incluirProduto(produtosCadastrados.get(idx % produtosCadastrados.size()));
            finalizarPedido(p);
        }
    }

    static void criarArquivoExemplo(Path arq) throws IOException {
        String dataProxima = LocalDate.now().plusDays(3).format(Produto.FORMATO_DATA);   // com desconto
        String dataLonge = LocalDate.now().plusDays(60).format(Produto.FORMATO_DATA);
        Files.write(arq, Arrays.asList(
                "1;Arroz 5kg;20.00;0.20",
                "1;Feijao 1kg;7.50;0.25",
                "1;Detergente;2.30;0.30",
                "2;Leite 1L;4.00;0.15;" + dataProxima,
                "2;Queijo;25.00;0.30;" + dataLonge,
                "1;Cafe 500g;15.00;0.20",
                "2;Iogurte;3.50;0.25;" + dataLonge,
                "1;Sabonete;1.80;0.40"), StandardCharsets.UTF_8);
    }

    // ===================== LEITURA DO TECLADO =====================

    static String lerTexto(String msg) {
        System.out.print(msg);
        return teclado.nextLine();
    }

    static int lerInt(String msg) {
        while (true) {
            try { return Integer.parseInt(lerTexto(msg).trim()); }
            catch (NumberFormatException e) { System.out.println("Digite um numero inteiro."); }
        }
    }

    static double lerDouble(String msg) {
        while (true) {
            try { return Double.parseDouble(lerTexto(msg).trim().replace(",", ".")); }
            catch (NumberFormatException e) { System.out.println("Digite um numero (ex.: 50,00)."); }
        }
    }
}
