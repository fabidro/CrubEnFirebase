package com.example.crubenfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.crubenfirebase.model.Persona;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private List<Persona> listpersona= new ArrayList<Persona>();
    ArrayAdapter<Persona> arrayAdapterPersona;
    EditText Nombre,Apellidos,Correo,Contraseña;
ListView ListaDePersonas;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
Persona personaSeleccionada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Nombre = (EditText) findViewById(R.id.Nombre);
        Apellidos = (EditText) findViewById(R.id.Apellidos);
        Correo = (EditText) findViewById(R.id.Correo);
        Contraseña = (EditText) findViewById(R.id.Contraseña);
        ListaDePersonas = (ListView) findViewById(R.id.ListaDePersonas);
        inicializarFirebase();
        listarDatos();
        ListaDePersonas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                personaSeleccionada=(Persona) adapterView.getItemAtPosition(i);
                Nombre.setText(personaSeleccionada.getNombre());
                Apellidos.setText(personaSeleccionada.getApellidos());
                Correo.setText(personaSeleccionada.getCorreo());
                Contraseña.setText(personaSeleccionada.getContraseña());
            }
        });
    }

    private void listarDatos() {
        databaseReference.child("Persona").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listpersona.clear();
                for (DataSnapshot objSnapshot : snapshot.getChildren()){
                    Persona persona = objSnapshot.getValue(Persona.class);
                    listpersona.add(persona);
                    arrayAdapterPersona = new ArrayAdapter<Persona>(MainActivity.this,
                            android.R.layout.simple_list_item_1,listpersona);
                    ListaDePersonas.setAdapter(arrayAdapterPersona);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String nombre = Nombre.getText().toString();
        String apellidos= Apellidos.getText().toString();
        String correo = Correo.getText().toString();
        String contraseña= Contraseña.getText().toString();
        switch (item.getItemId()){
            case R.id.icon_add:{
                if (nombre.equals("")||apellidos.equals("")||correo.equals("")||contraseña.equals("")){
                    validacion();

                }else{
                    Persona persona = new Persona();
                    persona.setUid(UUID.randomUUID().toString());
                    persona.setNombre(nombre);
                    persona.setApellidos(apellidos);
                    persona.setCorreo(correo);
                    persona.setContraseña(contraseña);
                    databaseReference.child("Persona").child(persona.getUid()).setValue(persona);
                    Toast.makeText(this,"Agregado",Toast.LENGTH_LONG).show();
                    limpiarCajas();

                }
                break;
            }
            case R.id.icon_delete:{
                Persona persona = new Persona();
                persona.setUid(personaSeleccionada.getUid());
                databaseReference.child("Persona").child(persona.getUid()).removeValue();
                Toast.makeText(this,"Eliminado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }
            case R.id.icon_save:{
                Persona persona = new Persona();
                persona.setUid(personaSeleccionada.getUid());
                persona.setNombre(Nombre.getText().toString().trim());
                persona.setApellidos(Apellidos.getText().toString().trim());
                persona.setCorreo(Correo.getText().toString().trim());
                persona.setContraseña(Contraseña.getText().toString().trim());
                databaseReference.child("Persona").child(persona.getUid()).setValue(persona);
                Toast.makeText(this,"Salvado",Toast.LENGTH_LONG).show();
                limpiarCajas();
                break;
            }


        }
        return true;
    }

    private void limpiarCajas() {
        Nombre.setText("");
        Correo.setText("");
        Contraseña.setText("");
        Apellidos.setText("");
    }

    private void validacion() {
        String nombre = Nombre.getText().toString();
        String apellidos= Apellidos.getText().toString();
        String correo = Correo.getText().toString();
        String contraseña= Contraseña.getText().toString();
        if(nombre.equals("")){
            Nombre.setError("Required");
        }
        if(apellidos.equals("")){
            Apellidos.setError("Required");
        }
        if(correo.equals("")){
            Correo.setError("Required");
        }
        if(contraseña.equals("")){
            Contraseña.setError("Required");
        }
    }
}