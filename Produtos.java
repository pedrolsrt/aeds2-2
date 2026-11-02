import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/*
 * =====================================================================
 *  PRODUTOS.JAVA  –  classes do sistema de comércio
 * =====================================================================
 *  Produto (abstrata) -> ProdutoNaoPerecivel e ProdutoPerecivel (herança/polimorfismo)
 *  Pedido             -> guarda até 10 produtos e calcula o valor total
 *
 *  NA PROVA: se a professora der o código-base dela, USE O DELA e aproveite
 *  daqui só o que faltar. Se os nomes forem diferentes (ex.: valorVenda em vez
 *  de valorDeVenda), troque com Ctrl+H.
 * =====================================================================
 */


/* ---------------------------------------------------------------------
 * PRODUTO (classe abstrata)
 * O que é: o "molde" comum de todo produto (descrição, custo, margem).
 * Por que abstrata: não existe produto "genérico"; ou é perecível ou não.
 * ------------------------------------------------------------------- */
abstract class Produto {

    protected static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected String descricao;
    protected double precoCusto;
    protected double margemLucro;   // 0.2 = 20%

    protected Produto(String descricao, double precoCusto, double margemLucro) {
        if (descricao == null || descricao.trim().length() < 3)
            throw new IllegalArgumentException("Descricao invalida!");
        if (precoCusto <= 0 || margemLucro <= 0)
            throw new IllegalArgumentException("Preco e margem devem ser positivos!");
        this.descricao = descricao.trim();
        this.precoCusto = precoCusto;
        this.margemLucro = margemLucro;
    }

    /** Preço de venda = custo + margem. Cada tipo de produto pode mudar isso (polimorfismo). */
    public double valorDeVenda() {
        return precoCusto * (1 + margemLucro);
    }

    public String getDescricao()  { return descricao; }
    public double getPrecoCusto() { return precoCusto; }

    /**
     * FÁBRICA: cria o produto certo a partir de uma linha do arquivo.
     * Formato:  1;descricao;custo;margem                (não perecível)
     *           2;descricao;custo;margem;dd/MM/yyyy     (perecível)
     */
    public static Produto criarDoTexto(String linha) {
        String[] c = linha.split(";");
        int tipo = Integer.parseInt(c[0].trim());
        String desc = c[1].trim();
        double custo = Double.parseDouble(c[2].trim().replace(",", "."));
        double margem = Double.parseDouble(c[3].trim().replace(",", "."));
        if (tipo == 1) return new ProdutoNaoPerecivel(desc, custo, margem);
        if (tipo == 2) return new ProdutoPerecivel(desc, custo, margem, LocalDate.parse(c[4].trim(), FORMATO_DATA));
        throw new IllegalArgumentException("Tipo de produto invalido: " + tipo);
    }

    /** Dois produtos são "iguais" se têm a mesma descrição (ignora maiúsculas).
     *  Isso é o que faz pedido.contemProduto(produto) funcionar. */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Produto)) return false;
        return descricao.equalsIgnoreCase(((Produto) obj).descricao);
    }

    @Override
    public int hashCode() {
        return descricao.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return String.format("%-15s R$ %8.2f", descricao, valorDeVenda());
    }
}


/* ---------------------------------------------------------------------
 * PRODUTO NÃO PERECÍVEL: usa o preço padrão (custo + margem).
 * ------------------------------------------------------------------- */
class ProdutoNaoPerecivel extends Produto {

    public ProdutoNaoPerecivel(String descricao, double precoCusto, double margemLucro) {
        super(descricao, precoCusto, margemLucro);
    }

    @Override
    public String toString() {
        return "NP  " + super.toString();
    }
}


/* ---------------------------------------------------------------------
 * PRODUTO PERECÍVEL: tem validade. Se faltam 7 dias ou menos, 25% de desconto.
 * (Regra de exemplo: ajuste se a professora usar outra.)
 * ------------------------------------------------------------------- */
class ProdutoPerecivel extends Produto {

    private static final double DESCONTO = 0.25;
    private static final int DIAS_PARA_DESCONTO = 7;

    private LocalDate dataValidade;

    public ProdutoPerecivel(String descricao, double precoCusto, double margemLucro, LocalDate dataValidade) {
        super(descricao, precoCusto, margemLucro);
        this.dataValidade = dataValidade;
    }

    public LocalDate getDataValidade() { return dataValidade; }

    @Override
    public double valorDeVenda() {
        double valor = super.valorDeVenda();
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), dataValidade);
        if (dias <= DIAS_PARA_DESCONTO) valor *= (1 - DESCONTO);
        return valor;
    }

    @Override
    public String toString() {
        return "P   " + super.toString() + "  validade: " + dataValidade.format(FORMATO_DATA);
    }
}


/* ---------------------------------------------------------------------
 * PEDIDO: id automático, data, até 10 produtos, valor total.
 * ------------------------------------------------------------------- */
class Pedido {

    private static final int MAX_PRODUTOS = 10;
    private static int ultimoId = 0;     // gera 1, 2, 3...

    private int idPedido;
    private LocalDate dataPedido;
    private Produto[] produtos;
    private int quantProdutos;

    public Pedido() {
        idPedido = ++ultimoId;
        dataPedido = LocalDate.now();
        produtos = new Produto[MAX_PRODUTOS];
        quantProdutos = 0;
    }

    /** Adiciona produto. Devolve false se o pedido já está cheio. */
    public boolean incluirProduto(Produto p) {
        if (quantProdutos >= MAX_PRODUTOS) return false;
        produtos[quantProdutos++] = p;
        return true;
    }

    /** VALOR TOTAL do pedido (soma do preço de venda dos produtos). */
    public double valorFinal() {
        double total = 0;
        for (int i = 0; i < quantProdutos; i++) total += produtos[i].valorDeVenda();
        return total;
    }

    /** O pedido tem esse produto? (usa o equals do Produto) */
    public boolean contemProduto(Produto p) {
        for (int i = 0; i < quantProdutos; i++)
            if (produtos[i].equals(p)) return true;
        return false;
    }

    /** O pedido tem algum produto com essa descrição? */
    public boolean contemProduto(String descricao) {
        for (int i = 0; i < quantProdutos; i++)
            if (produtos[i].getDescricao().equalsIgnoreCase(descricao.trim())) return true;
        return false;
    }

    public int getIdPedido()          { return idPedido; }
    public LocalDate getDataPedido()  { return dataPedido; }
    public int getQuantProdutos()     { return quantProdutos; }

    /** Cópia só com as posições usadas (sem os null). */
    public Produto[] getProdutos()    { return Arrays.copyOf(produtos, quantProdutos); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Pedido %d  (%s)  %d produto(s)  TOTAL: R$ %.2f",
                idPedido, dataPedido.format(Produto.FORMATO_DATA), quantProdutos, valorFinal()));
        for (int i = 0; i < quantProdutos; i++) sb.append("\n      ").append(produtos[i]);
        return sb.toString();
    }
}
