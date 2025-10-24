package com.example.controledegastos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText nomeLogin;
    private TextInputEditText senhaLogin;
    private Button entrar;
    private Button cadastrar;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o banco de dados
        db = new DatabaseHelper(this);

        // Vincula os componentes do layout aos objetos Java
        nomeLogin = findViewById(R.id.NomeLogin);
        senhaLogin = findViewById(R.id.SenhaLogin);
        entrar = findViewById(R.id.Entrar);
        cadastrar = findViewById(R.id.Cadastrar);

        // Botão de entrar
        entrar.setOnClickListener(v -> attemptLogin());

        // Botão de cadastro
        cadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }


    private void attemptLogin() {
        String nome = nomeLogin.getText().toString().trim();
        String senha = senhaLogin.getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, insira o nome.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.isEmpty()) {
            Toast.makeText(this, "Por favor, insira a senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        long userId = db.checkUser(nome, senha);

        if (userId != -1) {
            // Login bem-sucedido
            Intent intent = new Intent(MainActivity.this, TelaPrincipalActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
            finish();
        } else {
            // Falha no login
            Toast.makeText(this, "Nome ou senha incorretos.", Toast.LENGTH_SHORT).show();
        }
    }
}
