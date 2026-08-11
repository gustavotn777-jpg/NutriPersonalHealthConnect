 const dados = {
  saude: [],
  treinos: []
};

function salvarDados() {
  localStorage.setItem("nutriPersonalDados", JSON.stringify(dados));
}

function carregarDados() {
  const salvos = localStorage.getItem("nutriPersonalDados");

  if (salvos) {
    Object.assign(dados, JSON.parse(salvos));
  }
}

carregarDados();
