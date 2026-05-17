# Alterações feitas na API

## 1. Segurança e CORS
- Ajuste do front autorizado no CORS para `http://localhost:5173`.
- Inclusão de configuração de CORS diretamente na segurança da API.
- Liberação de métodos adicionais, incluindo `PATCH`.
- Manutenção da autenticação JWT com política `STATELESS`.
- Inclusão do endpoint de consulta de SINAPI na área permitida para usuário.

## 2. Consulta de itens SINAPI
- Criação do endpoint `GET /api/v1/sinapi/items`.
- Suporte a filtros por texto de busca (`search`).
- Suporte opcional ao filtro por UF (`uf`).
- Suporte à paginação via parâmetros `page` e `size`.

## 3. Camada de serviço
- Criação do `SinapiService` para concentrar a lógica de busca.
- Tratamento do filtro por UF com normalização para maiúsculas.
- Conversão dos dados da entidade para DTO de resposta.

## 4. Repositório
- Adição de consultas paginadas no `SinapiItemRepository`.
- Busca por descrição com filtro de UF.
- Busca por descrição sem filtro de UF.

## 5. DTOs adicionados
- `PageResponseDTO` para padronizar respostas paginadas.
- `SinapiItemResponseDTO` para expor os dados do item SINAPI na API.

## 6. Estrutura geral
- Criação do controller `SinapiController`.
- Organização da resposta com conteúdo, página atual, tamanho da página, total de elementos e total de páginas.


## Nova requisição + resposta
`GET` /api/v1/sinapi/items?search=cimento&uf=SP&page=0&size=20

`Resposta JSON (paginada):`
{
  "content": [
    {
      "id": "uuid-aqui",
      "codSinapi": "00001",
      "description": "CIMENTO PORTLAND COMPOSTO CP II-E-32",
      "classification": "Material",
      "unit": "KG",
      "uf": "SP",
      "price": 0.68
    }
  ],
  "page": 0,
  "size": 20,
  "totalElements": 150,
  "totalPages": 8
}
