from nodo import Nodo

class Arbol:
    def __init__(self):
        self.raiz = None

    def vaciar(self):
        self.raiz = None
        print("El árbol ha sido vaciado.")
        return True

    def insertar(self, nombre):
        if self.raiz is None:
            self.raiz = Nodo(nombre)
        else:
            self.insertarRecursivo(self.raiz, nombre)

    def insertarRecursivo(self, nodo, nombre):
        if nombre < nodo.nombre:
            if nodo.izquierdo is None:
                nodo.izquierdo = Nodo(nombre)
            else:
                self.insertarRecursivo(nodo.izquierdo, nombre)
        elif nombre > nodo.nombre:
            if nodo.derecho is None:
                nodo.derecho = Nodo(nombre)
            else:
                self.insertarRecursivo(nodo.derecho, nombre)

    def buscarNodo(self, nombre):
        return self.buscarRecursivo(self.raiz, nombre)

    def buscarRecursivo(self, nodo, nombre):
        if nodo is None:
            return "NO se encontro el nombre '" + nombre + "'."
        elif nodo.nombre == nombre:
            return "El nombre '" + nombre + "' SI se encuentra."
        elif nombre < nodo.nombre:
            return self.buscarRecursivo(nodo.izquierdo, nombre)
        else:
            return self.buscarRecursivo(nodo.derecho, nombre)

    def imprimirArbol(self):
        self.inorden(self.raiz)

    def inorden(self, nodo):
        if nodo:
            self.inorden(nodo.izquierdo)
            print(nodo.nombre, end  =" ")
            self.inorden(nodo.derecho)
