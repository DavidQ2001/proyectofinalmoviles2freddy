package com.example.proyectofinalmoviles2freddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText jetnombre, jetcedula;
    CheckBox jcbactivo,jcbgeneral,jcbpreferencial,jcbvip;
    String nombre, cedula, categoria,ident_doc;
    boolean respuesta;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        jetnombre = findViewById(R.id.etnombre);
        jetcedula = findViewById(R.id.etcedula);
        jcbactivo = findViewById(R.id.cbactivo);
        jcbgeneral = findViewById(R.id.cbgeneral);
        jcbpreferencial = findViewById(R.id.cbpreferencial);
        jcbvip = findViewById(R.id.cbvip);
    }

    public void Adicionar(View view) {
        nombre = jetnombre.getText().toString();
        cedula = jetcedula.getText().toString();

        if (nombre.isEmpty() || cedula.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show();
        }
        else {
            if (jcbpreferencial.isChecked())
                categoria = "Preferencial";
            else
                if (jcbgeneral.isChecked())
                categoria = "General";

            else
                categoria = "Vip";
            // Create a new user with a first and last name
            Map<String, Object> invitados = new HashMap<>();
            invitados.put("Nombre", nombre);
            invitados.put("Cedula", cedula);
            invitados.put("Categoria", categoria);
            invitados.put("Activo","si");

            // Add a new document with a generated ID
            db.collection("concierto")
                    .add(invitados)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(MainActivity.this, "Documento adicionado", Toast.LENGTH_SHORT).show();
                            Limpiar_campos();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Error adicionando documento", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void Consultar(View view){
        Buscar();
    }

    private void Buscar(){
        respuesta=false;
        cedula=jetcedula.getText().toString();
        if (cedula.isEmpty()){
            Toast.makeText(this, "Codigo es requerido", Toast.LENGTH_SHORT).show();
            jetcedula.requestFocus();
        }else{
            db.collection("concierto")
                    .whereEqualTo("Cedula",cedula)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    respuesta=true;
                                    ident_doc=document.getId();
                                    jetnombre.setText(document.getString("Nombre"));
                                    jetcedula.setText(document.getString("Cedula"));

                                    if (document.getString("Categoria").equals("Vip"))
                                        jcbvip.setChecked(true);
                                    else
                                    if (document.getString("Categoria").equals("Preferencial"))
                                        jcbpreferencial.setChecked(true);
                                    else
                                        jcbgeneral.setChecked(true);
                                    if (document.getString("Activo").equals("si"))
                                        jcbactivo.setChecked(true);
                                    else
                                        jcbactivo.setChecked(false);
                                    //Log.d(TAG, document.getId() + " => " + document.getData());
                                }
                            } else {
                                // Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }


    public void Anular(View view){

        cedula=jetcedula.getText().toString();
        nombre=jetnombre.getText().toString();

        if (cedula.isEmpty() || nombre.isEmpty()){
            Toast.makeText(this, "Los campos son requeridos", Toast.LENGTH_SHORT).show();
            jetcedula.requestFocus();
        }
        else {
            if (respuesta == true) {
                if (jcbvip.isChecked())
                    categoria = "Vip";
                else
                    if (jcbpreferencial.isChecked())
                    categoria = "Preferencial";
                else
                    categoria = "General";
                // Create a new user with a first and last name
                Map<String, Object> invitados = new HashMap<>();

                invitados.put("Cedula", cedula);
                invitados.put("Nombre", nombre);
                invitados.put("Categoria", categoria);
                invitados.put("Activo","no");

                // Modify a new document with a generated ID
                db.collection("concierto").document(ident_doc)
                        .set(invitados)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Documento anulado ", Toast.LENGTH_SHORT).show();
                                Limpiar_campos();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error anulando documento", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(this, "Debe primero consultar", Toast.LENGTH_SHORT).show();
                jetcedula.requestFocus();
            }
        }
    }



    public void Cancelar(View view){
        Limpiar_campos();
    }

    private void Limpiar_campos() {

        jetcedula.setText("");
        jetnombre.setText("");
        jcbactivo.setChecked(false);
        jcbgeneral.setChecked(false);
        jcbpreferencial.setChecked(false);
        jcbvip.setChecked(true);
        jetcedula.requestFocus();
        respuesta=false;
    }
}
