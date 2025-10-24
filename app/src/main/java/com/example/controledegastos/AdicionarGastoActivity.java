package com.example.controledegastos;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class AdicionarGastoActivity extends AppCompatActivity {

    private TextInputLayout nomeGastoLayout;
    private TextInputLayout valorGastoLayout;
    private Button salvarBtn;
    private Button voltarBtn;

    private DatabaseHelper db;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adicionar_gasto);

        nomeGastoLayout = findViewById(R.id.NomeGastoLayout);
        valorGastoLayout = findViewById(R.id.ValorGastoLayout);
        salvarBtn = findViewById(R.id.SalvarGasto);
        voltarBtn = findViewById(R.id.Voltar);

        db = new DatabaseHelper(this);

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Erro: usuário não identificado.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        salvarBtn.setOnClickListener(v -> salvarGasto());
        voltarBtn.setOnClickListener(v -> finish());
    }

    private void salvarGasto() {
        String nome = "";
        String valorStr = "";

        if (nomeGastoLayout.getEditText() != null)
            nome = nomeGastoLayout.getEditText().getText().toString().trim();
        if (valorGastoLayout.getEditText() != null)
            valorStr = valorGastoLayout.getEditText().getText().toString().trim();

        if (nome.isEmpty()) {
            Toast.makeText(this, "Digite o nome do gasto.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Digite o valor do gasto.", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor;
        try {
            valorStr = valorStr.replace(",", ".");
            valor = Double.parseDouble(valorStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Valor inválido. Use somente números.", Toast.LENGTH_SHORT).show();
            return;
        }

        long res = db.addGasto(userId, nome, valor);
        if (res == -1) {
            Toast.makeText(this, "Erro ao salvar gasto.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Gasto salvo!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
