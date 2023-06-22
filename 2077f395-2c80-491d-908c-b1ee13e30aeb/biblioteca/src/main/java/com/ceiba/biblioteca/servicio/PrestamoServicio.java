package com.ceiba.biblioteca.servicio;

import com.ceiba.biblioteca.entidad.Prestamo;
import com.ceiba.biblioteca.repositorio.PrestamoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PrestamoServicio implements PrestamoServicioInterface<Prestamo> {

    @Autowired
    public PrestamoRepositorio prestamoRepositorio;

    @Override
    public Prestamo crearPrestamo(Prestamo prestamo) throws Exception {
        try {
            if (prestamo.getTipoUsuario() == 3 && prestamoRepositorio.existsByIdentificacionUsuario(prestamo.getIdentificacionUsuario())) {
                throw new IllegalArgumentException("El usuario con identificacion " + prestamo.getIdentificacionUsuario() + "");
            }
            LocalDate fechaMaximaDevolucion = calcularFechaMaximaDevolucion(prestamo);
            prestamo.setFechaMaximaDevolucion(String.valueOf(fechaMaximaDevolucion));
            return prestamoRepositorio.save(prestamo);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public LocalDate calcularFechaMaximaDevolucion(Prestamo prestamo) {
        Integer tipoUsuario = prestamo.getTipoUsuario();
        LocalDate fechaActual = LocalDate.now();
        LocalDate fechaMaximaDevolucion;
        if (tipoUsuario == 1) {
            fechaMaximaDevolucion = fechaActual.plusDays(10);
        } else if (tipoUsuario == 2) {
            fechaMaximaDevolucion = fechaActual.plusDays(8);
        } else if (tipoUsuario == 3) {
            fechaMaximaDevolucion = fechaActual.plusDays(7);
        } else {
            throw new IllegalArgumentException("Tipo de usuario no permitido en la biblioteca");
        }
        if (fechaMaximaDevolucion.getDayOfWeek()== DayOfWeek.SATURDAY){
            fechaMaximaDevolucion = fechaMaximaDevolucion.plusDays(2);
        }else if (fechaMaximaDevolucion.getDayOfWeek()==DayOfWeek.SUNDAY){
            fechaMaximaDevolucion = fechaMaximaDevolucion.plusDays(1);
        }
        return fechaMaximaDevolucion;
    }



    @Override
    public Prestamo buscarporId(Integer id) throws Exception{
        try{
            Optional<Prestamo> prestamoOpcional= prestamoRepositorio.findById(id);
            if (prestamoOpcional.isPresent()){
                return prestamoOpcional.get();
            }else {
                throw new Exception("Usuario no encontrado");
            }
        }catch (Exception error){
            throw new Exception(error.getMessage());
        }
    }

    public String formatearFecha(LocalDate fechaParaFormatear){
        int dia, mes, anho;
        dia = fechaParaFormatear.getDayOfMonth();
        mes = fechaParaFormatear.getMonthValue();
        anho = fechaParaFormatear.getYear();
        String fecha = dia + "/"+ mes + "/" + anho;
        return fecha;
    }
}
