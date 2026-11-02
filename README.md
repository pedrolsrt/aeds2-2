# AEDS 2 – Estruturas de Dados Lineares em Sistemas de Software (Java)

| Arquivo | O que é | Mexer na prova? |
|---|---|---|
| `EstruturasLineares.java` | Pilha, Fila e Lista genéricas (encadeadas) com filtrar, calcularValorMedio, subPilha, somar, contar, buscar | Não |
| `Produtos.java` | Produto (abstrata), ProdutoPerecivel, ProdutoNaoPerecivel, Pedido | Só se o enunciado mudar as regras |
| `Comercio.java` | Sistema completo com menu (todas as tarefas das oficinas + extras) | Copiar a opção parecida |

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
