# AEDS 2 – Estruturas de Dados Lineares em Sistemas de Software (Java)

| Arquivo | O que é | Mexer na prova? |
|---|---|---|
| `EstruturasLineares.java` | Pilha, Fila e Lista genéricas (encadeadas) com filtrar, calcularValorMedio, subPilha, somar, contar, buscar | Não |
| `Produtos.java` | Produto (abstrata), ProdutoPerecivel, ProdutoNaoPerecivel, Pedido | Só se o enunciado mudar as regras |
| `Comercio.java` | Sistema completo com menu (todas as tarefas das oficinas + extras) | Copiar a opção parecida |

## Mapa das tarefas (Ctrl+F por "TAREFA" nos arquivos)
| Oficina | Tarefa | O que pede | Onde está |
|---|---|---|---|
| Pilhas | 1 | Guardar pedidos na estrutura certa (pilha) | `Comercio.finalizarPedido` |
| Pilhas | 2 | `Pilha<E> subPilha(int numItens)` | `EstruturasLineares.java` → `Pilha.subPilha` |
| Pilhas | 3 | Listar produtos dos pedidos mais recentes | `Comercio.listarProdutosPedidosRecentes` |
| Filas | 0 | Guardar pedidos por ordem de chegada (fila) | `Comercio.finalizarPedido` |
| Filas | 1 | `double calcularValorMedio(Function<E, Double>, int)` | `EstruturasLineares.java` → `Fila.calcularValorMedio` |
| Filas | 2 | `Fila<E> filtrar(Predicate<E>, int)` | `EstruturasLineares.java` → `Fila.filtrar` |
| Filas | 3a | Valor total médio dos N primeiros pedidos | `Comercio.exibirValorMedioPrimeirosPedidos` (menu 6) |
| Filas | 3b | Primeiros pedidos com valor acima de X | `Comercio.exibirPedidosAcimaDeValor` (menu 7) |
| Filas | 3c | Primeiros pedidos com um produto | `Comercio.exibirPedidosComProduto` (menu 8) |

## Quando usar cada estrutura
| Estrutura | Regra | Palavras do enunciado |
|---|---|---|
| Pilha | LIFO – último a entrar, primeiro a sair | "mais recentes", "últimos", "desfazer" |
| Fila  | FIFO – primeiro a entrar, primeiro a sair | "ordem de chegada", "primeiros", "atender" |
| Lista | acesso por posição | "inserir na posição", "remover o i-ésimo" |

## Custo das operações
| Operação | Pilha | Fila | Lista |
|---|---|---|---|
| Inserir | O(1) topo | O(1) fim | O(1) início/fim, O(n) meio |
| Remover | O(1) topo | O(1) frente | O(1) início, O(n) meio |
| Consultar | O(1) topo | O(1) frente | O(n) por posição |
| filtrar / média / soma | O(n) | O(n) | O(n) |

## Rodar
```
javac -d out *.java
java -cp out Comercio
```
