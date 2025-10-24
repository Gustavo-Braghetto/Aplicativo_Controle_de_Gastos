package com.example.controledegastos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class CadastroActivity extends AppCompatActivity {

    private TextInputLayout nomeCadastroLayout;
    private TextInputLayout senhaCadastroLayout;
    private Button confirmarBtn;
    private Button voltarBtn;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        db = new DatabaseHelper(this);

        // Inicializa os layouts e botões com os IDs corrigidos
        nomeCadastroLayout = findViewById(R.id.NomeCadastro);
        senhaCadastroLayout = findViewById(R.id.SenhaCadastro);
        confirmarBtn = findViewById(R.id.confirmarCadastro);
        voltarBtn = findViewById(R.id.VoltarLogin);

        // Botão de confirmar cadastro
        confirmarBtn.setOnClickListener(v -> confirmCadastro());

        // Botão de voltar para login
        voltarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void confirmCadastro() {
        String nome = "";
        String senha = "";

        // Pega o texto dos EditText internos de forma segura
        if (nomeCadastroLayout.getEditText() != null)
            nome = nomeCadastroLayout.getEditText().getText().toString().trim();
        if (senhaCadastroLayout.getEditText() != null)
            senha = senhaCadastroLayout.getEditText().getText().toString().trim();

        // Validações
        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite um nome.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (senha.isEmpty()) {
            Toast.makeText(this, "Digite uma senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tenta adicionar o usuário no banco
        long result = db.addUser(nome, senha);
        if (result == -1) {
            Toast.makeText(this, "Não foi possível cadastrar (nome pode já existir).", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Cadastro efetuado com sucesso!", Toast.LENGTH_SHORT).show();

            // Volta para a tela de login
            Intent intent = new Intent(CadastroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
