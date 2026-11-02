import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/*
 * =====================================================================
 *  ESTRUTURASLINEARES.JAVA  –  PILHA, FILA e LISTA genéricas (encadeadas)
 * =====================================================================
 *  NÃO PRECISA MEXER AQUI. Funciona com qualquer tipo: Pilha<Pedido>,
 *  Fila<Produto>, Lista<String>...
 *
 *  QUAL USAR?
 *   - PILHA (LIFO: último a entrar, primeiro a sair)
 *       -> "mais RECENTES", "últimos", "desfazer", "histórico".
 *   - FILA  (FIFO: primeiro a entrar, primeiro a sair)
 *       -> "ordem de CHEGADA", "primeiros", "atendimento", "fila de pedidos".
 *   - LISTA (acesso por posição, insere/remove em qualquer lugar)
 *       -> quando precisar inserir/remover no meio ou pegar pela posição.
 *
 *  MÉTODOS QUE AS 3 TÊM (mesmo nome, mesma ideia):
 *   vazia(), tamanho(), paraCada(acao), toString()
 *   calcularValorMedio(extrator, quantidade) -> média de um atributo dos N primeiros
 *   somar(extrator, quantidade)              -> soma de um atributo dos N primeiros
 *   filtrar(condicional, quantidade)         -> nova estrutura só com quem passa no teste
 *   contar(condicional, quantidade)          -> quantos dos N primeiros passam no teste
 *   buscar(condicional)                      -> primeiro elemento que passa no teste
 *
 *  LAMBDAS (o que vai nos parâmetros):
 *   Function<E, Double> extrator   ->  p -> p.valorFinal()     ou  Pedido::valorFinal
 *   Predicate<E> condicional       ->  p -> p.valorFinal() > 100
 *   Consumer<E> acao               ->  p -> System.out.println(p)
 *
 *  "quantidade" maior que o tamanho? -> usa o tamanho todo (não dá erro).
 * =====================================================================
 */


/* ---------------------------------------------------------------------
 * CELULA
 * O que é: a "caixinha" de cada elemento: guarda o item e aponta pro próximo.
 * Pilha, Fila e Lista são correntes de células ligadas.
 * ------------------------------------------------------------------- */
class Celula<E> {
    E item;
    Celula<E> proximo;

    Celula(E item) {
        this.item = item;
        this.proximo = null;
    }
}


/* =====================================================================
 * PILHA  (LIFO)
 *   topo -> [C] -> [B] -> [A] -> null      (C foi o último a entrar)
 * Todas as operações principais são O(1).
 * ===================================================================== */
class Pilha<E> {

    private Celula<E> topo;   // elemento mais recente
    private int tamanho;

    public Pilha() {
        topo = null;
        tamanho = 0;
    }

    public boolean vazia()  { return topo == null; }
    public int tamanho()    { return tamanho; }

    /** EMPILHAR (push): coloca no topo. */
    public void empilhar(E item) {
        Celula<E> nova = new Celula<>(item);
        nova.proximo = topo;
        topo = nova;
        tamanho++;
    }

    /** DESEMPILHAR (pop): tira e devolve o do topo. Erro se vazia. */
    public E desempilhar() {
        if (vazia()) throw new IllegalStateException("A pilha esta vazia!");
        E item = topo.item;
        topo = topo.proximo;
        tamanho--;
        return item;
    }

    /** CONSULTAR TOPO (peek): olha o do topo SEM tirar. */
    public E consultarTopo() {
        if (vazia()) throw new IllegalStateException("A pilha esta vazia!");
        return topo.item;
    }

    /**
     * SUBPILHA: nova pilha com os numItens elementos do TOPO, na mesma ordem
     * (o topo continua no topo). A pilha original NÃO muda.
     * Quando usar: "os N pedidos mais recentes".
     */
    public Pilha<E> subPilha(int numItens) {
        if (numItens < 0) throw new IllegalArgumentException("Numero de itens invalido!");
        int n = Math.min(numItens, tamanho);

        Pilha<E> auxiliar = new Pilha<>();          // fica invertida
        Celula<E> atual = topo;
        for (int i = 0; i < n; i++) {
            auxiliar.empilhar(atual.item);
            atual = atual.proximo;
        }
        Pilha<E> resultado = new Pilha<>();         // desinverte
        while (!auxiliar.vazia()) resultado.empilhar(auxiliar.desempilhar());
        return resultado;
    }

    /** FILTRAR: nova pilha (mesma ordem) só com os dos N do topo que passam no teste. */
    public Pilha<E> filtrar(Predicate<E> condicional, int quantidade) {
        Pilha<E> auxiliar = new Pilha<>();
        Celula<E> atual = topo;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) auxiliar.empilhar(atual.item);
            atual = atual.proximo;
        }
        Pilha<E> resultado = new Pilha<>();
        while (!auxiliar.vazia()) resultado.empilhar(auxiliar.desempilhar());
        return resultado;
    }

    /** MÉDIA de um atributo dos N elementos do topo. Ex.: calcularValorMedio(Pedido::valorFinal, 3) */
    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (vazia()) throw new IllegalStateException("A pilha esta vazia!");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        int n = Math.min(quantidade, tamanho);
        return somar(extrator, n) / n;
    }

    /** SOMA de um atributo dos N elementos do topo. */
    public double somar(Function<E, Double> extrator, int quantidade) {
        double soma = 0;
        Celula<E> atual = topo;
        for (int i = 0; i < quantidade && atual != null; i++) {
            soma += extrator.apply(atual.item);
            atual = atual.proximo;
        }
        return soma;
    }

    /** CONTAR quantos dos N do topo passam no teste. */
    public int contar(Predicate<E> condicional, int quantidade) {
        int cont = 0;
        Celula<E> atual = topo;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) cont++;
            atual = atual.proximo;
        }
        return cont;
    }

    /** BUSCAR o primeiro (a partir do topo) que passa no teste; null se nenhum. */
    public E buscar(Predicate<E> condicional) {
        for (Celula<E> c = topo; c != null; c = c.proximo)
            if (condicional.test(c.item)) return c.item;
        return null;
    }

    /** PERCORRER do topo pro fundo fazendo algo com cada um. Ex.: paraCada(p -> System.out.println(p)) */
    public void paraCada(Consumer<E> acao) {
        for (Celula<E> c = topo; c != null; c = c.proximo) acao.accept(c.item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Pilha (topo -> fundo): ");
        for (Celula<E> c = topo; c != null; c = c.proximo) {
            sb.append(c.item);
            if (c.proximo != null) sb.append(" | ");
        }
        return sb.toString();
    }
}


/* =====================================================================
 * FILA  (FIFO)
 *   frente -> [A] -> [B] -> [C] <- tras      (A foi o primeiro a chegar)
 * Todas as operações principais são O(1).
 * ===================================================================== */
class Fila<E> {

    private Celula<E> frente;   // primeiro a sair
    private Celula<E> tras;     // último que chegou
    private int tamanho;

    public Fila() {
        frente = null;
        tras = null;
        tamanho = 0;
    }

    public boolean vazia()  { return frente == null; }
    public int tamanho()    { return tamanho; }

    /** ENFILEIRAR: entra no FIM da fila. */
    public void enfileirar(E item) {
        Celula<E> nova = new Celula<>(item);
        if (vazia()) frente = nova;
        else tras.proximo = nova;
        tras = nova;
        tamanho++;
    }

    /** DESENFILEIRAR: tira e devolve o da FRENTE. Erro se vazia. */
    public E desenfileirar() {
        if (vazia()) throw new IllegalStateException("A fila esta vazia!");
        E item = frente.item;
        frente = frente.proximo;
        if (frente == null) tras = null;   // ficou vazia
        tamanho--;
        return item;
    }

    /** CONSULTAR PRIMEIRO: olha o da frente SEM tirar. */
    public E consultarPrimeiro() {
        if (vazia()) throw new IllegalStateException("A fila esta vazia!");
        return frente.item;
    }

    /**
     * CALCULAR VALOR MÉDIO de um atributo dos N PRIMEIROS da fila.
     * Ex.: fila.calcularValorMedio(Pedido::valorFinal, 5)  -> média dos 5 primeiros pedidos
     * A fila original NÃO muda.
     */
    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (vazia()) throw new IllegalStateException("A fila esta vazia!");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        int n = Math.min(quantidade, tamanho);
        return somar(extrator, n) / n;
    }

    /**
     * FILTRAR: nova fila com os elementos, dentre os N PRIMEIROS, que passam no teste
     * (mantém a ordem de chegada). A fila original NÃO muda.
     * Ex.: fila.filtrar(p -> p.valorFinal() > 100, 10)
     */
    public Fila<E> filtrar(Predicate<E> condicional, int quantidade) {
        Fila<E> resultado = new Fila<>();
        Celula<E> atual = frente;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) resultado.enfileirar(atual.item);
            atual = atual.proximo;
        }
        return resultado;
    }

    /** SUBFILA: nova fila só com os N primeiros. */
    public Fila<E> subFila(int quantidade) {
        return filtrar(x -> true, quantidade);
    }

    /** SOMA de um atributo dos N primeiros. */
    public double somar(Function<E, Double> extrator, int quantidade) {
        double soma = 0;
        Celula<E> atual = frente;
        for (int i = 0; i < quantidade && atual != null; i++) {
            soma += extrator.apply(atual.item);
            atual = atual.proximo;
        }
        return soma;
    }

    /** CONTAR quantos dos N primeiros passam no teste. */
    public int contar(Predicate<E> condicional, int quantidade) {
        int cont = 0;
        Celula<E> atual = frente;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) cont++;
            atual = atual.proximo;
        }
        return cont;
    }

    /** BUSCAR o primeiro (por ordem de chegada) que passa no teste; null se nenhum. */
    public E buscar(Predicate<E> condicional) {
        for (Celula<E> c = frente; c != null; c = c.proximo)
            if (condicional.test(c.item)) return c.item;
        return null;
    }

    /** PERCORRER da frente pro fim fazendo algo com cada um. */
    public void paraCada(Consumer<E> acao) {
        for (Celula<E> c = frente; c != null; c = c.proximo) acao.accept(c.item);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fila (frente -> tras): ");
        for (Celula<E> c = frente; c != null; c = c.proximo) {
            sb.append(c.item);
            if (c.proximo != null) sb.append(" | ");
        }
        return sb.toString();
    }
}


/* =====================================================================
 * LISTA  (encadeada, acesso por posição 0..tamanho-1)
 * Quando usar: inserir/remover em qualquer posição, ou pegar pelo índice.
 * Inserir/remover no início: O(1). No meio/por posição: O(n).
 * ===================================================================== */
class Lista<E> {

    private Celula<E> primeiro;
    private Celula<E> ultimo;
    private int tamanho;

    public Lista() {
        primeiro = null;
        ultimo = null;
        tamanho = 0;
    }

    public boolean vazia()  { return primeiro == null; }
    public int tamanho()    { return tamanho; }

    /** Insere no começo. */
    public void inserirInicio(E item) {
        Celula<E> nova = new Celula<>(item);
        nova.proximo = primeiro;
        primeiro = nova;
        if (ultimo == null) ultimo = nova;
        tamanho++;
    }

    /** Insere no fim. */
    public void inserirFinal(E item) {
        Celula<E> nova = new Celula<>(item);
        if (vazia()) primeiro = nova;
        else ultimo.proximo = nova;
        ultimo = nova;
        tamanho++;
    }

    /** Insere na posição (0 = início, tamanho = fim). */
    public void inserir(E item, int posicao) {
        if (posicao < 0 || posicao > tamanho) throw new IndexOutOfBoundsException("Posicao invalida: " + posicao);
        if (posicao == 0) { inserirInicio(item); return; }
        if (posicao == tamanho) { inserirFinal(item); return; }
        Celula<E> anterior = celula(posicao - 1);
        Celula<E> nova = new Celula<>(item);
        nova.proximo = anterior.proximo;
        anterior.proximo = nova;
        tamanho++;
    }

    /** Remove e devolve o elemento da posição. */
    public E remover(int posicao) {
        if (posicao < 0 || posicao >= tamanho) throw new IndexOutOfBoundsException("Posicao invalida: " + posicao);
        E item;
        if (posicao == 0) {
            item = primeiro.item;
            primeiro = primeiro.proximo;
            if (primeiro == null) ultimo = null;
        } else {
            Celula<E> anterior = celula(posicao - 1);
            Celula<E> removida = anterior.proximo;
            item = removida.item;
            anterior.proximo = removida.proximo;
            if (removida == ultimo) ultimo = anterior;
        }
        tamanho--;
        return item;
    }

    /** Devolve (sem remover) o elemento da posição. */
    public E obter(int posicao) {
        if (posicao < 0 || posicao >= tamanho) throw new IndexOutOfBoundsException("Posicao invalida: " + posicao);
        return celula(posicao).item;
    }

    /** Posição do primeiro que passa no teste; -1 se nenhum. */
    public int indiceDe(Predicate<E> condicional) {
        int i = 0;
        for (Celula<E> c = primeiro; c != null; c = c.proximo, i++)
            if (condicional.test(c.item)) return i;
        return -1;
    }

    public Lista<E> filtrar(Predicate<E> condicional, int quantidade) {
        Lista<E> resultado = new Lista<>();
        Celula<E> atual = primeiro;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) resultado.inserirFinal(atual.item);
            atual = atual.proximo;
        }
        return resultado;
    }

    public double calcularValorMedio(Function<E, Double> extrator, int quantidade) {
        if (vazia()) throw new IllegalStateException("A lista esta vazia!");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero!");
        int n = Math.min(quantidade, tamanho);
        return somar(extrator, n) / n;
    }

    public double somar(Function<E, Double> extrator, int quantidade) {
        double soma = 0;
        Celula<E> atual = primeiro;
        for (int i = 0; i < quantidade && atual != null; i++) {
            soma += extrator.apply(atual.item);
            atual = atual.proximo;
        }
        return soma;
    }

    public int contar(Predicate<E> condicional, int quantidade) {
        int cont = 0;
        Celula<E> atual = primeiro;
        for (int i = 0; i < quantidade && atual != null; i++) {
            if (condicional.test(atual.item)) cont++;
            atual = atual.proximo;
        }
        return cont;
    }

    public E buscar(Predicate<E> condicional) {
        for (Celula<E> c = primeiro; c != null; c = c.proximo)
            if (condicional.test(c.item)) return c.item;
        return null;
    }

    public void paraCada(Consumer<E> acao) {
        for (Celula<E> c = primeiro; c != null; c = c.proximo) acao.accept(c.item);
    }

    /** Anda até a célula da posição (uso interno). */
    private Celula<E> celula(int posicao) {
        Celula<E> atual = primeiro;
        for (int i = 0; i < posicao; i++) atual = atual.proximo;
        return atual;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Lista: ");
        for (Celula<E> c = primeiro; c != null; c = c.proximo) {
            sb.append(c.item);
            if (c.proximo != null) sb.append(" | ");
        }
        return sb.toString();
    }
}
