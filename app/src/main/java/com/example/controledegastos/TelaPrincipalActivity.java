package com.example.controledegastos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TelaPrincipalActivity extends AppCompatActivity {

    private Button deslogarBtn;
    private Button novoGastoBtn;
    private Button excluirGastoBtn;
    private ListView listaGastosView;
    private DatabaseHelper db;
    private long userId;
    private ArrayAdapter<String> adapter;
    private List<String> gastosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        deslogarBtn = findViewById(R.id.Deslogar);
        novoGastoBtn = findViewById(R.id.NovoGasto);
        excluirGastoBtn = findViewById(R.id.ExcluirGasto);
        listaGastosView = findViewById(R.id.listaGastos);

        db = new DatabaseHelper(this);

        userId = getIntent().getLongExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "Usuário não identificado!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        gastosList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gastosList);
        listaGastosView.setAdapter(adapter);

        novoGastoBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TelaPrincipalActivity.this, AdicionarGastoActivity.class);
            intent.putExtra("USER_ID", userId);
            startActivity(intent);
        });

        deslogarBtn.setOnClickListener(v -> {
            Intent intent = new Intent(TelaPrincipalActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        listaGastosView.setOnItemClickListener((parent, view, position, id) -> {
            String itemSelecionado = gastosList.get(position);
            String[] partes = itemSelecionado.split(" - R\\$ ");
            String nome = partes[0];

            excluirGastoBtn.setOnClickListener(v -> {
                db.deleteGasto(userId, nome);
                loadGastos();
                Toast.makeText(TelaPrincipalActivity.this, "Gasto excluído!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadGastos();
    }

    private void loadGastos() {
        gastosList.clear();
        gastosList.addAll(db.getGastosByUser(userId));
        adapter.notifyDataSetChanged();
    }
}
