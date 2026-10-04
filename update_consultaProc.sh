sed -i '/<\/f:facet>/a \
            <f:facet name="relaciones">\
                <p:outputPanel rendered="#{consultaProcedimientoModel.estadoModificar}">\
                    <p:panel header="Pasos de Procedimiento" style="margin-top:1rem">\
                        <p:dataTable value="#{consultaProcedimientoModel.pasosAsignados}" var="paso" emptyMessage="Sin pasos." size="small">\
                            <p:column headerText="Responsable"><h:outputText value="#{paso.idPersonaRol.idPersona.nombres} #{paso.idPersonaRol.idPersona.apellidos}"/></p:column>\
                            <p:column headerText="Estado"><h:outputText value="#{paso.estado}"/></p:column>\
                            <p:column headerText="Inicio"><h:outputText value="#{paso.fechaInicio}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></p:column>\
                            <p:column headerText="Fin"><h:outputText value="#{paso.fechaFin}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></p:column>\
                            <p:column headerText="Acción">\
                                <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{consultaProcedimientoModel.quitarPaso(paso)}" process="@this" update="@form">\
                                    <p:confirm header="Confirmar" message="¿Remover paso?" icon="pi pi-exclamation-triangle"/>\
                                </p:commandButton>\
                            </p:column>\
                        </p:dataTable>\
                        <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                            <p:outputLabel value="Responsable:" for="idRespNew"/>\
                            <p:autoComplete id="idRespNew" value="#{consultaProcedimientoModel.nuevoPaso.idPersonaRol}" completeMethod="#{consultaProcedimientoModel.completePersonaRol}" var="pr" itemLabel="#{pr.idPersona.nombres} #{pr.idPersona.apellidos} - #{pr.idRol.nombre}" itemValue="#{pr}" converter="#{entityConverter}" forceSelection="true" scrollHeight="250"/>\
                            <p:outputLabel value="Inicio:" for="fInicioPasoNew"/>\
                            <p:datePicker id="fInicioPasoNew" value="#{consultaProcedimientoModel.nuevoPaso.fechaInicio}" showTime="true" pattern="dd/MM/yyyy HH:mm"/>\
                            <p:outputLabel value="Fin:" for="fFinPasoNew"/>\
                            <p:datePicker id="fFinPasoNew" value="#{consultaProcedimientoModel.nuevoPaso.fechaFin}" showTime="true" pattern="dd/MM/yyyy HH:mm"/>\
                            <h:outputText value=""/>\
                            <p:commandButton value="Agregar Paso (Pendiente)" icon="pi pi-plus" styleClass="ui-button-success" action="#{consultaProcedimientoModel.agregarPaso()}" process="@this idRespNew fInicioPasoNew fFinPasoNew" update="@form"/>\
                        </p:panelGrid>\
                    </p:panel>\
                </p:outputPanel>\
            </f:facet>' src/main/webapp/paginas/clinica/consultaProcedimiento.xhtml
bash update_consultaProc.sh
